package io.pageobject.generator.element;

import io.pageobject.generator.GeneratorContext;
import io.pageobject.generator.locator.LocatorSources;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static io.pageobject.generator.Expressions.hasExpression;
import static io.pageobject.generator.attribute.Attributes.getNormalizedAttributeValue;

public class Ng1BindingProcessor extends AbstractElementProcessor {

	@Override
	public boolean isMatchingElement(Element element, GeneratorContext context) {
		return !isNullOrEmpty(getNormalizedAttributeValue(element, "ng-bind"))
				|| !isNullOrEmpty(getNormalizedAttributeValue(element, "ng-bind-html"))
				|| !isNullOrEmpty(
						getNormalizedAttributeValue(element, "ng-bind-template"))
				|| hasExpression(element.ownText());
	}

	@Override
	protected LocatorSources[] getElementLocatorCandidates(
			GeneratorContext context) {
		return new LocatorSources[] { LocatorSources.ID, LocatorSources.NAME,
				LocatorSources.NG_MODEL, LocatorSources.EXPRESSION_TEXT,
				LocatorSources.NG_BIND, LocatorSources.NG_BIND_HTML,
				LocatorSources.NG_BIND_TEMPLATE };
	}

	@Override
	protected List<String> getTemplates(GeneratorContext context,
			Map<String, Object> templateModel) {
		return newArrayList(ASSERTION_TEXT, ASSERTION_HAS_CLASS);
	}

}
