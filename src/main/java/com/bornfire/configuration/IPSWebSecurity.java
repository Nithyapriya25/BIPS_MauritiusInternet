package com.bornfire.configuration;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.client.RestTemplate;

import com.bornfire.entity.BIPS_Mer_User_Management_Entity;
import com.bornfire.entity.BIPS_Password_Management_Entity;
import com.bornfire.services.BankAndBranchMasterServices;

@Configuration
@EnableWebSecurity
public class IPSWebSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	Encryption encryption;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	BankAndBranchMasterServices bankAndBranchMasterServices;

	@Autowired
	Environment env;

	@Autowired
	private RestTemplate restTemplate;

	public String passwordvaluemain;
	private static final Logger logger = LoggerFactory.getLogger(IPSWebSecurity.class);

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/webjars/**", "/images/**", "/login*", "/changePasswordLogin*", "/changePasswordReq",
						"/submitTwofactorCheck*", "/sendOtp*", "/changePasswordloginscrren", "/freezeColumn/**",
						"/document/**", "/forgetpassword*", "/sendingmail_forget", "/sendingmail_otp",
						"/validtingsms_otp", "/sendingsms_otp", "/changePasswordotp", "/ldapList/**", "/ldapListGrp/**",
						"/RTPOUTINGPag", "/getstaticmerchantupiqrcode/**", "/getstaticqrcodeMerchantMaucas/**")
				.permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login").permitAll()
				.failureHandler(ipsAuthFailHandle()).successHandler(ipsAuthSuccessHandle()).usernameParameter("userid")
				.and().logout().permitAll().and().logout().logoutSuccessHandler(ipsLogoutSuccessHandler()).permitAll()
				.and().sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(false);

		http.csrf().disable();
	}
	@Bean
	public AuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider ap = new DaoAuthenticationProvider() {
	        @Override
	        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	        	System.out.println("1234Login");
	            String userid = authentication.getName();
	            System.out.println("userid==="+userid);
	            String password = authentication.getCredentials().toString();
	            System.out.println("password==="+password);

	            BIPS_Password_Management_Entity bipsUserOptional = null;
	            BIPS_Mer_User_Management_Entity bipsUserMan = null;

	            // Fetch data for BIPS_Password_Management_Entity
	            try {
	                String urlPassword = env.getProperty("ipsx.url") + "api/bipsUserForAuthentication?userid=" + userid;
	                bipsUserOptional = restTemplate.getForObject(urlPassword, BIPS_Password_Management_Entity.class);
	                // Debugging logs to check if data is fetched
	                System.out.println("BIPS_Password_Management_Entity: " + bipsUserOptional);
	            } catch (Exception e) {
	                // Only catch network or parsing issues, not null data
	                System.out.println("Error fetching from BIPS_Password_Management_Entity: " + e.getMessage());
	            }

	            // Fetch data for BIPS_Mer_User_Management_Entity
	            try {
	                String urlUserMan = env.getProperty("ipsx.url") + "api/bipsUserManageForAuthentication?userid=" + userid;
	                bipsUserMan = restTemplate.getForObject(urlUserMan, BIPS_Mer_User_Management_Entity.class);
	                // Debugging logs to check if data is fetched
	                System.out.println("BIPS_Mer_User_Management_Entity: " + bipsUserMan);
	            } catch (Exception e) {
	                // Only catch network or parsing issues, not null data
	                System.out.println("Error fetching from BIPS_Mer_User_Management_Entity: " + e.getMessage());
	            }

	            // Check in BIPS_Password_Management_Entity table
	            if (bipsUserOptional != null) {
	                return authenticateBipsUser(bipsUserOptional, password);
	            }

	            // Check in BIPS_Mer_User_Management_Entity table
	            if (bipsUserMan != null) {
	                return authenticateBipsUserMan(bipsUserMan, password);
	            }

	            // If both entities are null, return user not found exception
	            throw new UsernameNotFoundException("User not found in both tables");
	        }
	    };

	    ap.setHideUserNotFoundExceptions(false);
	    ap.setUserDetailsService(userDetailsService());
	    return ap;
	}



	private Authentication authenticateBipsUser(BIPS_Password_Management_Entity bipsUser, String password) {
		this.passwordvaluemain = password;
		if (!bipsUser.isAccountNonExpired1()) {
			throw new AccountExpiredException("Account Expired");
		} else if (!bipsUser.isCredentialsNonExpired1()) {
			throw new CredentialsExpiredException("Credentials Expired");
		} else if (!bipsUser.ispasswordnotexpiry()) {
			throw new LockedException("Password Expired");
		} else if (!bipsUser.isAccountNonLocked()) {
			throw new LockedException("Account Locked");
		} else if (!bipsUser.isUserStatus()) {
			throw new LockedException("Already in Use");
		} else {
			try {
				if (!PasswordEncryption.validatePassword(password, bipsUser.getPassword())) {
					handleInvalidPasswords(bipsUser);
				}
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				e.printStackTrace();
				throw new AuthenticationServiceException("Authentication error", e);
			}
			return new UsernamePasswordAuthenticationToken(bipsUser.getMerchant_rep_id(), password,
					Collections.emptyList());
		}
	}
	private Authentication authenticateBipsUserMan(BIPS_Mer_User_Management_Entity bipsUser, String password) {
		this.passwordvaluemain = password;
		if (!bipsUser.isAccountNonExpired1()) {
			throw new AccountExpiredException("Account Expired");
		} else if (!bipsUser.isCredentialsNonExpired1()) {
			throw new CredentialsExpiredException("Credentials Expired");
		} else if (!bipsUser.ispasswordnotexpiry()) {
			throw new LockedException("Password Expired");
		} else if (!bipsUser.isAccountNonLocked()) {
			throw new LockedException("Account Locked");
		} else if (!bipsUser.isUserStatus()) {
			throw new LockedException("Already in Use");
		} else {
			try {
				if (!PasswordEncryption.validatePassword(password, bipsUser.getPassword1())) {
					handleInvalidUsers(bipsUser);
				}
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				e.printStackTrace();
				throw new AuthenticationServiceException("Authentication error", e);
			}
			return new UsernamePasswordAuthenticationToken(bipsUser.getUser_id(), password,
					Collections.emptyList());
		}
	} 

	/*
	 * @Bean
	 * 
	 * @Override public UserDetailsService userDetailsService() { return new
	 * UserDetailsService() {
	 * 
	 * @Override public UserDetails loadUserByUsername(String username) throws
	 * UsernameNotFoundException { BIPS_Password_Management_Entity up1 = new
	 * BIPS_Password_Management_Entity(); String url = env.getProperty("ipsx.url") +
	 * "api/bipsUserForAuthentication?" + "userid=" + username; up1 =
	 * restTemplate.getForObject(url, BIPS_Password_Management_Entity.class); if
	 * (Objects.nonNull(up1)) { return (UserDetails) up1; } else { return
	 * (UserDetails) new BIPS_Password_Management_Entity(); }
	 * 
	 * BIPS_Mer_User_Management_Entity up1user = new
	 * BIPS_Mer_User_Management_Entity(); String urluser =
	 * env.getProperty("ipsx.url") + "api/bipsUserManageForAuthentication?" +
	 * "userid=" + username; up1user = restTemplate.getForObject(url,
	 * BIPS_Mer_User_Management_Entity.class); if (Objects.nonNull(up1user)) {
	 * return (UserDetails) up1user; } else { return (UserDetails) new
	 * BIPS_Mer_User_Management_Entity(); } } }; }
	 */
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
	    return new UserDetailsService() {
	        @Override
	        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	            BIPS_Password_Management_Entity up1 = null;
	            BIPS_Mer_User_Management_Entity up1user = null;
	            String url = env.getProperty("ipsx.url") + "api/bipsUserForAuthentication?userid=" + username;
	            String urluser = env.getProperty("ipsx.url") + "api/bipsUserManageForAuthentication?" + "userid=" + username;
	            try {
	                // Fetching the user details
	                up1 = restTemplate.getForObject(url, BIPS_Password_Management_Entity.class);
	                up1user = restTemplate.getForObject(urluser, BIPS_Mer_User_Management_Entity.class);
	            } catch (Exception e) {
	                throw new UsernameNotFoundException("User not found", e);
	            }

	            if (up1 != null) {
	                // Assuming up1 implements UserDetails or can be converted into a UserDetails object
	                return (UserDetails) up1; 
	            } else if (up1user != null) {
	                // Assuming up1user implements UserDetails or can be converted into a UserDetails object
	                return (UserDetails) up1user; 
	            } else {
	                throw new UsernameNotFoundException("User not found");
	            }
	        }
	    };
	}

	@Bean
	public AuthenticationFailureHandler ipsAuthFailHandle() {
		return new AuthenticationFailureHandler() {
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.sendRedirect("login?error=" + exception.getMessage());
			}
		};
	}

	@Bean
	public AuthenticationSuccessHandler ipsAuthSuccessHandle() {
		return new AuthenticationSuccessHandler() {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				HttpSession session = request.getSession(false);
				if (session != null) {
					session.invalidate();
				}
				session = request.getSession(true);
				String url = env.getProperty("ipsx.url") + "api/bipsUserForAuthentication?" + "userid="
						+ authentication.getName();
				BIPS_Password_Management_Entity user1 = restTemplate.getForObject(url,
						BIPS_Password_Management_Entity.class);
				
				String url1 = env.getProperty("ipsx.url") + "api/bipsUserManageForAuthentication?" + "userid="
						+ authentication.getName();
				BIPS_Mer_User_Management_Entity user2 = restTemplate.getForObject(url1,
						BIPS_Mer_User_Management_Entity.class);

				if (Objects.nonNull(user1)) {
					request.getSession().setAttribute("USERID", user1.getMerchant_rep_id());
					request.getSession().setAttribute("USERID1", user1.getUnit_id());
					request.getSession().setAttribute("MER_USER_ID", user1.getMerchant_user_id());
					request.getSession().setAttribute("MER_USER_NAME", user1.getMerchant_corporate_name());
					request.getSession().setAttribute("MERUNIT", user1.getUnit_id());
					request.getSession().setAttribute("unitidacess", user1.getUnit_id());
					request.getSession().setAttribute("ROLEID", "MER");
					request.getSession().setAttribute("USERNAME", user1.getMer_representative_name());
					request.getSession().setAttribute("UNITID", user1.getUnit_id());
					request.getSession().setAttribute("UNITNAME", user1.getUnit_name());
					request.getSession().setAttribute("USERCATEGORY", user1.getUser_category());
					request.getSession().setAttribute("acces", user1.getPwlog_flg());
					response.sendRedirect("IPSDashboard");
				}else if (Objects.nonNull(user2)) {
					request.getSession().setAttribute("USERID", user2.getUser_id());
					request.getSession().setAttribute("USERID1", user2.getUnit_id_u());
					request.getSession().setAttribute("MER_USER_ID", user2.getMerchant_user_id());
					request.getSession().setAttribute("MER_USER_NAME", user2.getMerchant_corporate_name());
					request.getSession().setAttribute("MERUNIT", user2.getUnit_id_u());
					request.getSession().setAttribute("unitidacess", user2.getUnit_id_u());
					request.getSession().setAttribute("ROLEID", "MER");
					request.getSession().setAttribute("USERNAME", user2.getUser_name());
					request.getSession().setAttribute("UNITID", user2.getUnit_id_u());
					request.getSession().setAttribute("UNITNAME", user2.getUnit_name_u());
					request.getSession().setAttribute("USERCATEGORY", user2.getUser_category());
					//request.getSession().setAttribute("acces", user1.getPwlog_flg());
					response.sendRedirect("IPSDashboard");
				} else {
					response.sendRedirect("login?error=" + "No Record Found");
				}
			}
		};
	}
	
	@Bean
	public LogoutSuccessHandler ipsLogoutSuccessHandler() {
		return new LogoutSuccessHandler() {
			@Override
			public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				HttpSession session = request.getSession(false);
				if (session != null) {
					session.invalidate();
				}
				// Optional<BIPS_Password_Management_Entity> up1 =
				// bIPS_PasswordManagement_Repo.findById(authentication.getName());
				BIPS_Password_Management_Entity up1 = new BIPS_Password_Management_Entity();
				String url = env.getProperty("ipsx.url") + "api/bipsUserForAuthentication?" + "userid="
						+ authentication.getName();
				up1 = restTemplate.getForObject(url, BIPS_Password_Management_Entity.class);

				
				String url1 = env.getProperty("ipsx.url") + "api/updateLoginStatus?" + "userid="
						+ up1.getMerchant_rep_id();
				restTemplate.getForObject(url1, BIPS_Password_Management_Entity.class);
				response.sendRedirect("login?logout");
				
				//for user man
				BIPS_Mer_User_Management_Entity up_user = new BIPS_Mer_User_Management_Entity();
				String url_user = env.getProperty("ipsx.url") + "api/bipsUserManageForAuthentication?" + "userid="
						+ authentication.getName();
				up_user = restTemplate.getForObject(url_user, BIPS_Mer_User_Management_Entity.class);
				
				String url_user1 = env.getProperty("ipsx.url") + "api/updateLoginStatusUserManage?" + "userid="
						+ up_user.getUser_id();
				restTemplate.getForObject(url_user1, BIPS_Mer_User_Management_Entity.class);
				response.sendRedirect("login?logout");
				
			}
		};
	}

	public void handleInvalidPasswords(BIPS_Password_Management_Entity bipsPasswordManagementEntity) {
		logger.info("Passing Userid: " + bipsPasswordManagementEntity.getMerchant_rep_id());
		String url = env.getProperty("ipsx.url") + "api/updateLoginLockedFlg?merchant_rep_id="
				+ bipsPasswordManagementEntity.getMerchant_rep_id();
		restTemplate.getForEntity(url, String.class);
		throw new BadCredentialsException("Authentication failed");
	}
	
	public void handleInvalidUsers(BIPS_Mer_User_Management_Entity bIPS_Mer_User_Management_Entity) {
		logger.info("Passing Userid: " + bIPS_Mer_User_Management_Entity.getUser_id());
		String url = env.getProperty("ipsx.url") + "api/updateLoginLockedFlgUserManage?merchant_rep_id="
				+ bIPS_Mer_User_Management_Entity.getUser_id();
		restTemplate.getForEntity(url, String.class);
		throw new BadCredentialsException("Authentication failed");
	}

}