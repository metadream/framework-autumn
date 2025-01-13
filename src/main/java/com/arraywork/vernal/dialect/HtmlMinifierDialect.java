package com.arraywork.vernal.dialect;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.IPostProcessorDialect;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Html Minifier Dialect
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/04/25
 */
@Component
public class HtmlMinifierDialect implements IPostProcessorDialect {

    @Override
    public String getName() {
        return "thymeleaf-minifier";
    }

    @Override
    public int getDialectPostProcessorPrecedence() {
        return 1000;
    }

    @Override
    public Set<IPostProcessor> getPostProcessors() {
        Set<IPostProcessor> set = new HashSet<>(1);
        set.add(new IPostProcessor() {
            @Override
            public TemplateMode getTemplateMode() {
                return TemplateMode.HTML;
            }

            @Override
            public int getPrecedence() {
                return 1000;
            }

            @Override
            public Class<? extends ITemplateHandler> getHandlerClass() {
                return HtmlMinifierHandler.class;
            }

        });
        return set;
    }

}