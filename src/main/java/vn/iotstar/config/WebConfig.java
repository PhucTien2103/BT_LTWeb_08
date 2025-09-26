package vn.iotstar.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        // Lưu trữ ngôn ngữ đã chọn trong cookie
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        // Đặt tên cookie là 'lang'
        localeResolver.setCookieName("lang");
        // Thời gian tồn tại của cookie (ví dụ: 1 năm)
        localeResolver.setCookieMaxAge(365 * 24 * 60 * 60);
        return localeResolver;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // Tải các tệp tin từ thư mục 'i18n'
        messageSource.setBasename("classpath:i18n/messages");
        // Mã hóa UTF-8
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
        // Tham số URL để thay đổi ngôn ngữ, ví dụ: /?lang=en
        localeInterceptor.setParamName("lang");
        registry.addInterceptor(localeInterceptor);
    }
    
    @Override
    public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/"); // hoặc file:{đường_dẫn_tuyệt_đối}/
    }

}