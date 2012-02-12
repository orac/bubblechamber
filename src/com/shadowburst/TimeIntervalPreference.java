package com.shadowburst;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.quietlycoding.android.picker.NumberPicker;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class TimeIntervalPreference extends DialogPreference {
	private static final String[] unit_strings = {"seconds", "minutes", "hours", "days", "years"};
	private static final int[] unit_multipliers = {1, 60, 60*60, 60*60*24, (int)((60*60*24)*365.2422)};
	private Context ctx;
	private NumberPicker digits, units;
	private int value;
	
	public TimeIntervalPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctx = context;
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		int def = 0;
		if ((String)defaultValue != null) {
			def = parse_string((String)defaultValue);
		}
		
		if (restorePersistedValue) {
	    	value = getPersistedInt(def);
		} else {
			if (shouldPersist()) {
				value = def;
				persistInt(value);
			}
		}
		update_gui();
	}

	@Override
	protected View onCreateDialogView() {
	    LinearLayout layout = new LinearLayout(ctx);
	    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	    params.weight = 1.0f;
	    assert(digits == null);
	    assert(units == null);
	    
	    digits = new NumberPicker(ctx);
	    digits.setRange(0, 10000);
	    digits.setWrap(false);
	    units = new NumberPicker(ctx);
	    units.setRange(0, unit_strings.length - 1, unit_strings);
	    units.setWrap(false);

	    update_gui();
	    layout.addView(digits);
	    layout.addView(units, params);
	    return layout;
	}
	
	private void update_gui() {
		if (digits != null && units != null) {
			int units_index = value >>> 28;
			int quantity = value & ((1 << 28) - 1);
			
			digits.setCurrent(quantity);
			units.setCurrent(units_index);
		}
	}

	private static int parse_string(String s) throws TimeIntervalParseException {
		int result;
		final Pattern p = Pattern.compile("^(\\d+)\\s*(\\w+)$");
		Matcher m = p.matcher(s);
		Integer num = Integer.parseInt(m.group(1));
		if (num == null || (num & 0xf0000000) != 0) {
			throw new TimeIntervalParseException();
		}
		String un = m.group(2);
		int i;
		for (i = 0; i < unit_strings.length; ++i) {
			if (unit_strings[i].equals(un)) {
				break;
			}
		}
		if (i == unit_strings.length) {
			throw new TimeIntervalParseException();
		}
		assert(unit_strings.length < (1 << 4));
		result = num | (i << 28);
		return result;
	}

	/**
	 * Return a number of milliseconds that corresponds to the value.
	 * 
	 * @param s The string value of the preference
	 * @return The number of milliseconds it represents
	 */
	static public long to_milliseconds(int preference) {
		int units_index = preference >>> 28;
		long quantity = preference & ((1 << 28) - 1);
		return unit_multipliers[units_index] * quantity * 1000l;
	}
	
	@Override
	public void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult && shouldPersist()) {
			value = (units.getCurrent() << 28) | digits.getCurrent();
			persistInt(value);
		}
		digits = null; units = null;
	}
}
