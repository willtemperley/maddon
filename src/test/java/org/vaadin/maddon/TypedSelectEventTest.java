package org.vaadin.maddon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.vaadin.maddon.fields.MValueChangeEvent;
import org.vaadin.maddon.fields.MValueChangeListener;
import org.vaadin.maddon.fields.TypedSelect;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;

public class TypedSelectEventTest {
	
	private Set<String> s = new HashSet<String>();
	
	private TypedSelect<String> getSelect(Class<? extends AbstractSelect> selectType) {
		TypedSelect<String> ts = new TypedSelect<String>(String.class).withSelectType(selectType);
		return ts;
	}
	
	@Test
	public void testEventFired() {
		
		final Double expectedVal = 2.2; 
		
		s.clear();

		
		TypedSelect<String> select = getSelect(ComboBox.class);
		
		ArrayList<String> options = new ArrayList<String>();
		options.add("a");
		options.add("b");
		select.setBeans(options);
		select.attach();
		
		select.addMValueChangeListener(new MValueChangeListener<String>() {
			public void valueChange(MValueChangeEvent<String> event) {
				String val = event.getValue();
				Assert.assertEquals(expectedVal, val);
				s.add(val);
			}
		});
		
		select.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				System.out.println("hi");
				
			}
		});

		select.setValue("a");
		
		Assert.assertTrue(s.contains("b"));
		
	}

}
