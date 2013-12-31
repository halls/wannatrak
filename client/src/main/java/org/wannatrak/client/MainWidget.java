/*
 * Copyright 2009 Andrey Khalzov, and individual contributors as indicated by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

/**
 * Created by Andrey Khalzov
 * 16.12.2008 15:23:10
 */
package org.wannatrak.client;

import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.controls.ZoomControlOptions;
import com.google.gwt.user.client.ui.*;
import org.wannatrak.client.demo.DemoWidget;
import org.wannatrak.client.layout.MainWidgetLayout;
import org.wannatrak.client.layout.MainWidgetLayoutManager;
import org.wannatrak.client.layout.command.HideLeftWidgetCommand;
import org.wannatrak.client.layout.command.HideRightWidgetCommand;
import org.wannatrak.client.layout.command.ShowLeftWidgetCommand;
import org.wannatrak.client.layout.command.ShowRightWidgetCommand;
import org.wannatrak.client.login.LoginWidget;

public class MainWidget extends FlexTable {
    private MainWidgetLayout mainWidgetLayout;
    private MainWidgetLayout prevMainWidgetLayout;

    private PushButton leftHideButton;
    private PushButton leftShowButton;

    private PushButton rightHideButton;
    private PushButton rightShowButton;

    private Label leftWidgetLabel;
    private Label rightWidgetLabel;

    private Widget rightWidget;
    private Widget leftWidget;
    private Widget bottomWidget;

    private String leftWidgetCellStyleName;
    private String centerWidgetCellStyleName;
    private String centerWidgetHeaderCellStyleName;
    private String rightWidgetCellStyleName;
    private String bottomWidgetCellStyleName;

    private MapWidget map;

    private Widget centerHeaderWidget;
    private Widget centerWidget;

    private Mediator mediator;

    public MainWidget(Mediator mediator) {
        this.mediator = mediator;

        this.setCellSpacing(7);
        this.setCellPadding(2);

        this.setStylePrimaryName("main");

        leftWidgetLabel = new Label(
                StringConstants.StringConstantsSingleton.getInstance().demoSubjects(),
                false
        );
        leftWidgetLabel.setStylePrimaryName("subjectsWidgetTitle");

        final Label sloganLabel = new Label(StringConstants.StringConstantsSingleton.getInstance().slogan(), false);
        mediator.setRightHeaderLabel(sloganLabel);

        rightWidgetLabel = sloganLabel;
        rightWidgetLabel.setStylePrimaryName("slogan");

        Hyperlink howToSetupHyperlink = new Hyperlink(
                StringConstants.StringConstantsSingleton.getInstance().howtoSetup(),
                HistoryToken.how_to_setup.toString()
        );
        howToSetupHyperlink.setStyleName("centerHeaderCellElements");

        Anchor getAndroidAppAnchor = new Anchor(
                StringConstants.StringConstantsSingleton.getInstance().getAndroidApp(),
                "http://play.google.com/store/apps/details?id=org.wannatrak.android",
                "_blank"
        );
        getAndroidAppAnchor.setStyleName("centerHeaderCellElements");

        HorizontalPanel centerHeaderWidget = new HorizontalPanel();
        centerHeaderWidget.add(howToSetupHyperlink);
        centerHeaderWidget.add(getAndroidAppAnchor);
        centerHeaderWidget.setCellHorizontalAlignment(getAndroidAppAnchor, HasHorizontalAlignment.ALIGN_RIGHT);
        centerHeaderWidget.setWidth("100%");
        this.centerHeaderWidget = centerHeaderWidget;

        map = createMap();
        centerWidget = map;

        leftHideButton = createLeftHideButton();

        rightHideButton = createStubButton();

        leftShowButton = createLeftShowButton();

        rightShowButton = createStubButton();

        rightWidget = new LoginWidget(mediator);

        leftWidget = new DemoWidget(mediator);

        leftWidgetCellStyleName = "subjectsWidgetCell";
        centerWidgetCellStyleName = "centerCell";
        centerWidgetHeaderCellStyleName = "centerHeaderCell";
        rightWidgetCellStyleName = "loginWidgetCell";
        bottomWidgetCellStyleName = "bottomWidgetCell";


        bottomWidget = new FilterWidget(mediator);

        mediator.setMainWidget(this);
        mediator.setMainWidgetLayoutManager(new MainWidgetLayoutManager());
    }

    public MainWidgetLayout getMainWidgetLayout() {
        return mainWidgetLayout;
    }

    public void setMainWidgetLayout(MainWidgetLayout mainWidgetLayout) {
        this.mainWidgetLayout = mainWidgetLayout;
    }

    public MainWidgetLayout getPrevMainWidgetLayout() {
        return prevMainWidgetLayout;
    }

    public void setPrevMainWidgetLayout(MainWidgetLayout prevMainWidgetLayout) {
        this.prevMainWidgetLayout = prevMainWidgetLayout;
    }

    public MapWidget getMap() {
        return map;
    }

    public PushButton getLeftHideButton() {
        return leftHideButton;
    }

    public void setLeftHideButton(PushButton leftHideButton) {
        this.leftHideButton = leftHideButton;
    }

    public PushButton getLeftShowButton() {
        return leftShowButton;
    }

    public void setLeftShowButton(PushButton leftShowButton) {
        this.leftShowButton = leftShowButton;
    }

    public PushButton getRightHideButton() {
        return rightHideButton;
    }

    public void setRightHideButton(PushButton rightHideButton) {
        this.rightHideButton = rightHideButton;
    }

    public PushButton getRightShowButton() {
        return rightShowButton;
    }

    public void setRightShowButton(PushButton rightShowButton) {
        this.rightShowButton = rightShowButton;
    }

    public Label getLeftWidgetLabel() {
        return leftWidgetLabel;
    }

    public void setLeftWidgetLabel(Label leftWidgetLabel) {
        this.leftWidgetLabel = leftWidgetLabel;
    }

    public Label getRightWidgetLabel() {
        return rightWidgetLabel;
    }

    public void setRightWidgetLabel(Label rightWidgetLabel) {
        this.rightWidgetLabel = rightWidgetLabel;
    }

    public Widget getRightWidget() {
        return rightWidget;
    }

    public void setRightWidget(Widget rightWidget) {
        this.rightWidget = rightWidget;
    }

    public Widget getLeftWidget() {
        return leftWidget;
    }

    public void setLeftWidget(Widget leftWidget) {
        this.leftWidget = leftWidget;
    }

    public Widget getBottomWidget() {
        return bottomWidget;
    }

    public void setBottomWidget(Widget bottomWidget) {
        this.bottomWidget = bottomWidget;
    }

    public String getLeftWidgetCellStyleName() {
        return leftWidgetCellStyleName;
    }

    public void setLeftWidgetCellStyleName(String leftWidgetCellStyleName) {
        this.leftWidgetCellStyleName = leftWidgetCellStyleName;
    }

    public String getCenterWidgetCellStyleName() {
        return centerWidgetCellStyleName;
    }

    public void setCenterWidgetCellStyleName(String centerWidgetCellStyleName) {
        this.centerWidgetCellStyleName = centerWidgetCellStyleName;
    }

    public String getRightWidgetCellStyleName() {
        return rightWidgetCellStyleName;
    }

    public void setRightWidgetCellStyleName(String rightWidgetCellStyleName) {
        this.rightWidgetCellStyleName = rightWidgetCellStyleName;
    }

    public String getCenterWidgetHeaderCellStyleName() {
        return centerWidgetHeaderCellStyleName;
    }

    public void setCenterWidgetHeaderCellStyleName(String centerWidgetHeaderCellStyleName) {
        this.centerWidgetHeaderCellStyleName = centerWidgetHeaderCellStyleName;
    }

    public String getBottomWidgetCellStyleName() {
        return bottomWidgetCellStyleName;
    }

    public void setBottomWidgetCellStyleName(String bottomWidgetCellStyleName) {
        this.bottomWidgetCellStyleName = bottomWidgetCellStyleName;
    }

    public Widget getCenterWidget() {
        return centerWidget;
    }

    public void setCenterWidget(Widget centerWidget) {
        this.centerWidget = centerWidget;
    }

    public Widget getCenterHeaderWidget() {
        return centerHeaderWidget;
    }

    public void setCenterHeaderWidget(Widget centerHeaderWidget) {
        this.centerHeaderWidget = centerHeaderWidget;
    }

    public void enableRightShowHideButton() {
        rightHideButton = createRightHideButton();
        rightShowButton = createRightShowButton();
    }

    public void disableRightShowHideButton() {
        rightHideButton = createStubButton();
        rightShowButton = createStubButton();
    }

    private PushButton createRightShowButton() {
        final PushButton rightShowButton = new PushButton("\u00AB");
        rightShowButton.addClickListener(new CommandClickListener(
                new ShowRightWidgetCommand(mediator)
        ));
        rightShowButton.setStylePrimaryName("hideButton");
        return rightShowButton;
    }

    private PushButton createLeftShowButton() {
        final PushButton leftShowButton = new PushButton("\u00BB");
        leftShowButton.addClickListener(new CommandClickListener(
                new ShowLeftWidgetCommand(mediator)
        ));
        leftShowButton.setStylePrimaryName("hideButton");
        return leftShowButton;
    }

    private PushButton createRightHideButton() {
        final PushButton rightHideButton = new PushButton("\u00BB");
        rightHideButton.addClickListener(new CommandClickListener(
                new HideRightWidgetCommand(mediator)
        ));
        rightHideButton.setStylePrimaryName("hideButton");
        return rightHideButton;
    }

    private PushButton createStubButton() {
        return new PushButton(" ");
    }

    private PushButton createLeftHideButton() {
        final PushButton leftHideButton = new PushButton("\u00AB");
        leftHideButton.addClickListener(new CommandClickListener(
                new HideLeftWidgetCommand(mediator)
        ));
        leftHideButton.setStylePrimaryName("hideButton");
        return leftHideButton;
    }

    private MapWidget createMap() {
        final LatLng tsu = LatLng.newInstance(53.5, 49.4);

        MapOptions mapOptions = MapOptions.newInstance();
        mapOptions.setCenter(tsu);
        mapOptions.setZoom(13);
        mapOptions.setScrollWheel(true);
        mapOptions.setZoomControl(true);
        final MapWidget map = new MapWidget(mapOptions);
        //map.setContinuousZoom(true);
        //map.setScrollWheelZoomEnabled(true);
        map.setStylePrimaryName("map");

        //map.addControl(new LargeMapControl());

        //map.addControl(new HierarchicalMapTypeControl());

        return map;
    }
}
