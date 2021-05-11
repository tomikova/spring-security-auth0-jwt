package example.auth.client.security.filter

import org.springframework.http.HttpMethod
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class BaseAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    RequestMatcher authNotRequiredMatcher

    BaseAuthenticationFilter(String processUrl = "/**", String[] anonymousUrls) {
        super(processUrl)
        authNotRequiredMatcher = new OrRequestMatcher(anonymousUrls.collect{new AntPathRequestMatcher(it)})
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        request.method != HttpMethod.OPTIONS.name() && !authNotRequiredMatcher.matches(request) && super.requiresAuthentication(request, response)
    }
}
