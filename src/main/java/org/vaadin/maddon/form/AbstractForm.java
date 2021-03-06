package org.vaadin.maddon.form;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import org.vaadin.maddon.BeanBinder;
import org.vaadin.maddon.MBeanFieldGroup;
import org.vaadin.maddon.MBeanFieldGroup.FieldGroupListener;
import org.vaadin.maddon.button.MButton;
import org.vaadin.maddon.button.PrimaryButton;
import org.vaadin.maddon.layouts.MHorizontalLayout;

/**
 * Abstract super class for simple editor forms.
 *
 * @param <T> the type of the bean edited
 */
public abstract class AbstractForm<T> extends CustomComponent implements
        FieldGroupListener {

    private MBeanFieldGroup<T> fieldGroup;

    @Override
    public void onFieldGroupChange(MBeanFieldGroup beanFieldGroup) {
        adjustSaveButtonState();
        adjustCancelButtonState();
    }

    protected void adjustSaveButtonState() {
        if (isAttached() && isEagarValidation() && isBound()) {
            boolean beanModified = fieldGroup.isBeanModified();
            boolean valid = fieldGroup.isValid();
            getSaveButton().setEnabled(beanModified && valid);
        }
    }

    protected boolean isBound() {
        return fieldGroup != null;
    }

    private void adjustCancelButtonState() {
        if (isAttached() && isEagarValidation() && isBound()) {
            boolean beanModified = fieldGroup.isBeanModified();
            getResetButton().setEnabled(beanModified);
        }
    }

    public interface SavedHandler<T> {

        void onSave(T entity);
    }

    public interface ResetHandler<T> {

        void onReset(T entity);
    }

    private T entity;
    private SavedHandler<T> savedHandler;
    private ResetHandler<T> resetHandler;
    private boolean eagarValidation;

    public boolean isEagarValidation() {
        return eagarValidation;
    }

    /**
     * In case one is working with "detached entities" enabling eager validation
     * will highly improve usability. The validity of the form will be updated
     * on each changes and save & cancel buttons will reflect to the validity
     * and possible changes.
     *
     * @param eagarValidation
     */
    public void setEagarValidation(boolean eagarValidation) {
        this.eagarValidation = eagarValidation;
    }

    public MBeanFieldGroup<T> setEntity(T entity) {
        this.entity = entity;
        if (entity != null) {
            fieldGroup = BeanBinder.bind(entity, this);
            if (isEagarValidation()) {
                fieldGroup.withEagarValidation(this);
                adjustSaveButtonState();
                adjustCancelButtonState();
            }
            setVisible(true);
            return fieldGroup;
        } else {
            setVisible(false);
            return null;
        }
    }

    public void setSavedHandler(SavedHandler<T> savedHandler) {
        this.savedHandler = savedHandler;
    }

    public void setResetHandler(ResetHandler<T> resetHandler) {
        this.resetHandler = resetHandler;
    }

    /**
     * @return A default toolbar containing save & cancel buttons
     */
    public HorizontalLayout getToolbar() {
        return new MHorizontalLayout(
                createSaveButton(),
                createCancelButton()
        );
    }

    protected Component createCancelButton() {
        setResetButton(new MButton("Cancel", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                reset(event);
            }
        }));
        return getResetButton();
    }
    private MButton resetButton;

    public MButton getResetButton() {
        return resetButton;
    }
    
    public void setResetButton(MButton resetButton) {
        this.resetButton = resetButton;
    }
    
    protected Component createSaveButton() {
        setSaveButton(new PrimaryButton("Save", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                save(event);
            }
        }));
        return getSaveButton();
    }

    private Button saveButton;

    public void setSaveButton(Button saveButton) {
        this.saveButton = saveButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    protected void save(Button.ClickEvent e) {
        savedHandler.onSave(entity);
    }

    protected void reset(Button.ClickEvent e) {
        resetHandler.onReset(entity);
    }

    @Override
    public void attach() {
        setCompositionRoot(createContent());
        super.attach();
        adjustSaveButtonState();
        adjustCancelButtonState();
    }

    public void focusFirst() {
        Component compositionRoot = getCompositionRoot();
        findFieldAndFocus(compositionRoot);
    }

    private boolean findFieldAndFocus(Component compositionRoot) {
        if (compositionRoot instanceof AbstractComponentContainer) {
            AbstractComponentContainer cc = (AbstractComponentContainer) compositionRoot;

            for (Component component : cc) {
                if (component instanceof AbstractTextField) {
                    AbstractTextField abstractTextField = (AbstractTextField) component;
                    abstractTextField.selectAll();
                    return true;
                }
                if (component instanceof AbstractField) {
                    AbstractField abstractField = (AbstractField) component;
                    abstractField.focus();
                    return true;
                }
                if (component instanceof AbstractComponentContainer) {
                    if (findFieldAndFocus(component)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * This method should return the actual content of the form, including
     * possible toolbar.
     *
     * Example implementation could look like this:      <code>
     * public class PersonForm extends AbstractForm&lt;Person&gt; {
     *
     *     private TextField firstName = new MTextField(&quot;First Name&quot;);
     *     private TextField lastName = new MTextField(&quot;Last Name&quot;);
     *
     *     @Override
     *     protected Component createContent() {
     *         return new MVerticalLayout(
     *                 new FormLayout(
     *                         firstName,
     *                         lastName
     *                 ),
     *                 getToolbar()
     *         );
     *     }
     * }
     * </code>
     *
     * @return the content of the form
     *
     */
    protected abstract Component createContent();

}
