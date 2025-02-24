package com.arraywork.autumn.util;

import java.util.Map;
import java.util.Set;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Markdown Utils
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2025/01/17
 */
public class MarkdownUtils {

    /** Create and add autolink extension */
    private static final Set<Extension> extensions = Set.of(AutolinkExtension.create());
    private static final Parser parser = Parser.builder().extensions(extensions).build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions)
        .attributeProviderFactory(c -> new LinkAttributeProvider()).build();

    /** Render string in markdown */
    public static String render(String markdown) {
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

    /** Add target attribute to link node */
    static class LinkAttributeProvider implements AttributeProvider {
        @Override
        public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
            if (node instanceof Link) {
                attributes.put("target", "_blank");
            }
        }
    }

}