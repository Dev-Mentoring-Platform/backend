package com.project.mentoridge.modules.base;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.PrincipalDetailsService;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.User;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest
public abstract class AbstractControllerTest {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;

//    @InjectMocks
//    protected AuthInterceptor authInterceptor;
//    @InjectMocks
//    protected JwtRequestFilter jwtRequestFilter;
    @MockBean
    protected JwtTokenManager jwtTokenManager;
    @MockBean
    protected PrincipalDetailsService principalDetailsService;

    protected User user;
    protected PrincipalDetails principalDetails;
    protected String accessToken = "accessToken";
    protected String accessTokenWithPrefix = "Bearer accessToken";

    // @BeforeEach
    protected void init() {

        when(jwtTokenManager.verifyToken(accessToken)).thenReturn(true);
        when(jwtTokenManager.getClaim(accessToken, "username")).thenReturn("user@email.com");
        when(jwtTokenManager.getClaim(accessToken, "role")).thenReturn(RoleType.MENTOR.getType());
        principalDetails = mock(PrincipalDetails.class);

        user = mock(User.class);
        when(principalDetails.getUser()).thenReturn(user);
        when(principalDetailsService.loadUserByUsername("user@email.com")).thenReturn(principalDetails);

        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }
}
