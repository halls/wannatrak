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
 * 16.12.2008 12:57:09
 */
package org.wannatrak.client;

import com.google.gwt.user.client.ui.*;

import java.util.Map;
import java.util.HashMap;

public class InputGrid extends Grid {
    private Map<String, TextBox> textBoxesMap;
    private Map<Widget, Integer> widgetRowsMap;
    private Map<TextBox, HTML> textBoxCommentsMap;
    private int i = 0;
    private String labelCellStyle;
    private String textBoxCellStyle;
    private String textBoxStyle;

    public InputGrid(int rows, int columns, String labelCellStyle, String textBoxCellStyle, String textBoxStyle) {
        super(rows, columns);

        this.labelCellStyle = labelCellStyle;
        this.textBoxCellStyle = textBoxCellStyle;
        this.textBoxStyle = textBoxStyle;

        textBoxesMap = new HashMap<String, TextBox>();
        widgetRowsMap = new HashMap<Widget, Integer>();
        textBoxCommentsMap = new HashMap<TextBox, HTML>();
    }

    public TextBox addLabeledTextBoxWithComment(String labelText, String htmlComment, String commentStyle) {
        final TextBox textBox = addLabeledTextBox(labelText);
        final HTML comment = new HTML(htmlComment);
        comment.setStylePrimaryName(commentStyle);
        setWidget(i, 1, comment);
        i++;

        textBoxCommentsMap.put(textBox, comment);
        return textBox;
    }

    public TextBox addLabeledTextBoxWithComment(
            String labelText,
            boolean password,
            String htmlComment,
            String commentStyle
    ) {
        final TextBox textBox = addLabeledTextBox(labelText, password);
        final HTML comment = new HTML(htmlComment);
        comment.setStylePrimaryName(commentStyle);
        setWidget(i, 1, comment);
        i++;

        textBoxCommentsMap.put(textBox, comment);
        return textBox;
    }

    public TextBox addLabeledTextBoxInOneColumn(String labelText) {
        return addLabeledTextBoxInOneColumn(labelText, false);
    }

    public TextBox addLabeledTextBoxInOneColumn(String labelText, boolean password) {
        final Label label = new Label(
                labelText + StringConstants.StringConstantsSingleton.getInstance().labelColon(),
                false
        );

        final TextBox textBox = password ? new PasswordTextBox() : new TextBox();
        textBox.setStylePrimaryName(textBoxStyle);

        setWidget(i, 0, label);
        setWidget(i + 1, 0, textBox);

        getCellFormatter().setStyleName(i, 0, labelCellStyle);
        getCellFormatter().setStyleName(i + 1, 0, textBoxCellStyle);

        textBoxesMap.put(labelText, textBox);

        i+=2;
        return textBox;
    }

    public TextBox addLabeledTextBox(String labelText) {
        return addLabeledTextBox(labelText, false);
    }

    public TextBox addLabeledTextBox(String labelText, boolean password) {
        final Label label = new Label(
                labelText + StringConstants.StringConstantsSingleton.getInstance().labelColon(),
                false
        );

        final TextBox textBox = password ? new PasswordTextBox() : new TextBox();
        textBox.setStylePrimaryName(textBoxStyle);

        setWidget(i, 0, label);
        setWidget(i, 1, textBox);

        getCellFormatter().setStyleName(i, 0, labelCellStyle);
        getCellFormatter().setStyleName(i, 1, textBoxCellStyle);

        textBoxesMap.put(labelText, textBox);

        i++;
        return textBox;
    }

    public void addWidget(Widget widget, String cellStyle) {
        addWidget(widget);
        getCellFormatter().setStyleName(i - 1, 1, cellStyle);
    }

    public void addWidget(Widget widget) {
        this.setWidget(i, 1, widget);
        widgetRowsMap.put(widget, i);
        i++;
    }

    public void replace(Widget oldWidget, Widget newWidget) {
        int row = widgetRowsMap.get(oldWidget);
        setWidget(row, 1, newWidget);
        widgetRowsMap.remove(oldWidget);
        widgetRowsMap.put(newWidget, row);
    }
    
    public String getInputValue(String labelText) {
        final TextBox textBox = textBoxesMap.get(labelText);
        if (textBox != null) {
            return textBox.getText();
        } else {
            return "null";
        }
    }

    public void setInputValue(String labelText, String value) {
        final TextBox textBox = textBoxesMap.get(labelText);
        if (textBox != null) {
            textBox.setText(value);
        }
    }

    public HTML getComment(TextBox textBox) {
        return textBoxCommentsMap.get(textBox);
    }
}
