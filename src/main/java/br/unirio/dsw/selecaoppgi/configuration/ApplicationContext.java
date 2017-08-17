package br.unirio.dsw.selecaoppgi.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
 
/**
 * Classe que representa a configuração da aplicação
 * 
 * @author marciobarros
 */
@Configuration
@ComponentScan(basePackages = {"br.unirio.dsw.selecaoppgi.service", "br.unirio.dsw.selecaoppgi.dao"})
@Import({SpringConfiguration.class, SecurityContext.class, SocialContext.class})
@PropertySource("classpath:configuration.properties")
public class ApplicationContext 
{
	/**
	 * Retorna o resource bundle responsável pela tradução de textos
	 */
	@Bean
    public MessageSource messageSource() 
    {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
 
	/**
	 * Prepara a leitura do arquivo de propriedades
	 */
    @Bean
    public PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() 
    {
        return new PropertySourcesPlaceholderConfigurer();
    }
}