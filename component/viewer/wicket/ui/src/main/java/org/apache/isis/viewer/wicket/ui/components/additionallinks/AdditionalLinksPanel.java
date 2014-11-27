/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.viewer.wicket.ui.components.additionallinks;

import java.util.List;
import com.google.common.base.Strings;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.isis.core.commons.lang.StringExtensions;
import org.apache.isis.viewer.wicket.model.links.LinkAndLabel;
import org.apache.isis.viewer.wicket.model.links.ListOfLinksModel;
import org.apache.isis.viewer.wicket.ui.ComponentFactory;
import org.apache.isis.viewer.wicket.ui.panels.PanelAbstract;
import org.apache.isis.viewer.wicket.ui.panels.PanelUtil;
import org.apache.isis.viewer.wicket.ui.util.Components;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;

public class AdditionalLinksPanel extends PanelAbstract<ListOfLinksModel> {

    private static final long serialVersionUID = 1L;

    private static final String ID_ADDITIONAL_LINK_LIST = "additionalLinkList";
    private static final String ID_ADDITIONAL_LINK_ITEM = "additionalLinkItem";
    private static final String ID_ADDITIONAL_LINK_FONT_AWESOME = "additionalLinkFontAwesome";
    private static final String ID_ADDITIONAL_LINK_TITLE = "additionalLinkTitle";

    public enum Style {
        INLINE_LIST {
            @Override
            AdditionalLinksPanel newPanel(String id, List<LinkAndLabel> links) {
                return new AdditionalLinksAsListInlinePanel(id, links);
            }
        },
        DROPDOWN {
            @Override
            AdditionalLinksPanel newPanel(String id, List<LinkAndLabel> links) {
                return new AdditionalLinksAsDropDownPanel(id, links);
            }
        };
        abstract AdditionalLinksPanel newPanel(String id, List<LinkAndLabel> links);
    }

    public static AdditionalLinksPanel addAdditionalLinks(
            final MarkupContainer markupContainer,
            final String id,
            final List<LinkAndLabel> links,
            final Style style) {
        if(links.isEmpty()) {
            Components.permanentlyHide(markupContainer, id);
            return null;
        }

        final AdditionalLinksPanel additionalLinksPanel =  style.newPanel(id, links);
        markupContainer.addOrReplace(additionalLinksPanel);
        return additionalLinksPanel;
    }


    private List<LinkAndLabel> linkAndLabels;
    
    protected AdditionalLinksPanel(final String id, final List<LinkAndLabel> links) {
        super(id, new ListOfLinksModel(links));

        this.linkAndLabels = getModel().getObject();
        
        final WebMarkupContainer container = new WebMarkupContainer(ID_ADDITIONAL_LINK_LIST);
        addOrReplace(container);
        
        container.setOutputMarkupId(true);
        
        setOutputMarkupId(true);
        
        final ListView<LinkAndLabel> listView = new ListView<LinkAndLabel>(ID_ADDITIONAL_LINK_ITEM, this.linkAndLabels) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<LinkAndLabel> item) {
                final LinkAndLabel linkAndLabel = item.getModelObject();
                
                final AbstractLink link = linkAndLabel.getLink();

                final String cssClassFa = linkAndLabel.getCssClassFa();
                if(Strings.isNullOrEmpty(cssClassFa)) {
                    Components.permanentlyHide(link, ID_ADDITIONAL_LINK_FONT_AWESOME);
                } else {
                    Label dummy = new Label(ID_ADDITIONAL_LINK_FONT_AWESOME, "");
                    link.addOrReplace(dummy);
                    dummy.add(new CssClassAppender(cssClassFa));
                }

                final String itemTitle = first(linkAndLabel.getDisabledReasonIfAny(), linkAndLabel.getDescriptionIfAny());
                if(itemTitle != null) {
                    item.add(new AttributeAppender("title", itemTitle));
                }

                final Label viewTitleLabel = new Label(ID_ADDITIONAL_LINK_TITLE, linkAndLabel.getLabel());
                if(linkAndLabel.isBlobOrClob()) {
                    link.add(new CssClassAppender("noVeil"));
                }
                if(linkAndLabel.isPrototype()) {
                    link.add(new CssClassAppender("prototype"));
                }
                link.add(new CssClassAppender(linkAndLabel.getActionIdentifier()));

                final String cssClass = linkAndLabel.getCssClass();
                if(cssClass != null) {
                    link.add(new CssClassAppender(cssClass));
                }
                viewTitleLabel.add(new CssClassAppender(StringExtensions.asLowerDashed(linkAndLabel.getLabel())));

                link.addOrReplace(viewTitleLabel);
                item.addOrReplace(link);
            }
        };
        container.addOrReplace(listView);
    }

    private static String first(String... str) {
        for (String s : str) {
            if(s != null) return s;
        }
        return null;
    }

    /**
     * Because there is no {@link ComponentFactory} for this component,
     * its CSS must be contributed in this way instead (also meaning its CSS is not bundled).
     */
    @Override
    public void renderHead(final IHeaderResponse response) {
        PanelUtil.renderHead(response, this.getClass());
    }


}
