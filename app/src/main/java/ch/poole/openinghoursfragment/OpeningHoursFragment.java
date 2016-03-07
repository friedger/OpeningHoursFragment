package ch.poole.openinghoursfragment;

import ch.poole.openinghoursparser.Holiday;
import ch.poole.openinghoursparser.MonthDayRange;
import ch.poole.openinghoursparser.OpeningHoursParser;
import ch.poole.openinghoursparser.ParseException;
import ch.poole.openinghoursparser.Rule;
import ch.poole.openinghoursparser.RuleModifier;
import ch.poole.openinghoursparser.TimeSpan;
import ch.poole.openinghoursparser.Util;
import ch.poole.openinghoursparser.WeekDayRange;
import ch.poole.openinghoursparser.WeekRange;
import ch.poole.openinghoursparser.YearRange;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;
import com.touchboarder.weekdaysbuttons.WeekdaysDataSource;


public class OpeningHoursFragment extends AppCompatDialogFragment {
	
	private static final String DEBUG_TAG = OpeningHoursFragment.class.getSimpleName();
	
	private LayoutInflater inflater = null;
	
	private String openingHoursValue;
	
	/**
     */
    static public OpeningHoursFragment newInstance(String value) {
    	OpeningHoursFragment f = new OpeningHoursFragment();

        Bundle args = new Bundle();
        args.putSerializable("value", value);

        f.setArguments(args);
        // f.setShowsDialog(true);
        
        return f;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(DEBUG_TAG, "onAttach");
//        try {
//            mListener = (OnPresetSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement OnPresetSelectedListener");
//        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "onCreate");
    }
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	// Inflate the layout for this fragment
    	this.inflater = inflater;
		LinearLayout openingHoursLayout = (LinearLayout) inflater.inflate(R.layout.openinghours,null);

//    	if (savedInstanceState != null) {
//    		Log.d(DEBUG_TAG,"Restoring from saved state");
//    		parents = (HashMap<Long, String>) savedInstanceState.getSerializable("PARENTS");
//    	} else if (savedParents != null ) {
//    		Log.d(DEBUG_TAG,"Restoring from instance variable");
//    		parents = savedParents;
//    	} else {
    	openingHoursValue = getArguments().getString("value");
//    	}
    	buildForm(openingHoursLayout,openingHoursValue);
 
		return openingHoursLayout;
    }

    /**
     * 
     * @param openingHoursLayout
     * @param openingHoursValue2
     */
    @SuppressLint("NewApi")
	private void buildForm(LinearLayout openingHoursLayout, String openingHoursValue) {
    	EditText text = (EditText) openingHoursLayout.findViewById(R.id.openinghours_string_edit);
    	LinearLayout openingHoursView = (LinearLayout) openingHoursLayout.findViewById(R.id.openinghours_view);
		if (text != null && openingHoursView != null) {
			text.setText(openingHoursValue);
			openingHoursView.removeAllViews();
			ScrollView sv = new ScrollView(getActivity());
			openingHoursView.addView(sv);
			LinearLayout ll = new LinearLayout(getActivity());
			ll.setOrientation(LinearLayout.VERTICAL);
//			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//					LinearLayout.LayoutParams.MATCH_PARENT,
//					LinearLayout.LayoutParams.WRAP_CONTENT);
//		    layoutParams.setMargins(20, 20, 20, 20);
//		    ll.setLayoutParams(layoutParams);
			sv.addView(ll);
			// some standard static elements
			TextView dash = new TextView(getActivity());
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT,0.0f);
			dash.setLayoutParams(params);
			dash.setText("-");
			dash.setGravity(Gravity.CENTER_VERTICAL);
			TextView comma = new TextView(getActivity());
			comma.setLayoutParams(params);
			comma.setText(",");
			comma.setGravity(Gravity.CENTER_VERTICAL);
			//
			OpeningHoursParser parser = new OpeningHoursParser(new ByteArrayInputStream(openingHoursValue.getBytes()));
			try {
				final ArrayList<Rule> rules = parser.rules();
				final ArrayList<ArrayList<Rule>> mergeableRules = Util.getMergeableRules(rules);
				int n = 1;
				for (final ArrayList<Rule>ruleList:mergeableRules)
				{
					boolean first = true;
					for (Rule r : ruleList)
					{
						if (first) {
							TextView header = new TextView(getActivity());
							header.setText("Rule " + n);
							header.setTextSize(24.0f);
							ll.addView(header);

							String comment = r.getComment();
							if (comment != null && comment.length() > 0) {
								TextView tv = new TextView(getActivity());
								tv.setText(comment);
								ll.addView(tv);
							}

							// year range list
							ArrayList<YearRange> years = r.getYears();
							LinearLayout yearLayout = new LinearLayout(getActivity());
							yearLayout.setOrientation(LinearLayout.HORIZONTAL);
							ll.addView(yearLayout);
							if (years != null && years.size() > 0) {

								for (YearRange yr:years) {
									// NumberPicker np1 = getYearPicker(yr.getStartYear());
									EditText np1 = new EditText(getActivity());
									np1.setText(Integer.toString(yr.getStartYear()));
									int endYear = yr.getEndYear();
									if (endYear < 0) {
                                        endYear = yr.getStartYear();
                                    }
									// NumberPicker np2 = getYearPicker(endYear);
									EditText np2 = new EditText(getActivity());
									np2.setText(Integer.toString(endYear));
									yearLayout.addView(np1);
									yearLayout.addView(dash);
									yearLayout.addView(np2);
									if (!(years.get(years.size()-1)==yr)) {
                                        yearLayout.addView(comma);
                                    }
								}
							}
							// week list
							ArrayList<WeekRange> weeks = r.getWeeks();
							if (weeks != null && weeks.size() > 0) {
								StringBuffer b = new StringBuffer();
								for (WeekRange wr:weeks) {
									b.append(wr.toString());
									if (weeks.get(weeks.size()-1)!=wr) {
										b.append(",");
									}
								}
								TextView tv = new TextView(getActivity());
								tv.setText(b.toString());
								ll.addView(tv);
							}
							// month day list
							ArrayList<MonthDayRange> monthdays = r.getMonthdays();
							if (monthdays != null && monthdays.size() > 0) {
								StringBuffer b = new StringBuffer();
								for (MonthDayRange md:monthdays) {
									b.append(md.toString());
									if (monthdays.get(monthdays.size()-1)!=md) {
										b.append(",");
									} 
								}
								TextView tv = new TextView(getActivity());
								tv.setText(b.toString());
								ll.addView(tv);
							}
							// holiday list
							ArrayList<Holiday> holidays = r.getHolidays();
							if (holidays != null && holidays.size() > 0) {
								StringBuffer b = new StringBuffer();
								for (Holiday hd:holidays) {
									b.append(hd.toString());
									if (holidays.get(holidays.size()-1)!=hd) {
										b.append(",");
									} 
								}
								TextView tv = new TextView(getActivity());
								tv.setText(b.toString());
								ll.addView(tv);
							}
							// modifier
							RuleModifier modifier = r.getModifier();
							if (modifier != null && modifier.getModifier() != null && modifier.getModifier().length() > 0) {
								TextView tv = new TextView(getActivity());
								tv.setText(modifier.getModifier());
								ll.addView(tv);
							}
							if (modifier != null && modifier.getComment() != null && modifier.getComment().length() > 0) {
								TextView tv = new TextView(getActivity());
								tv.setText(modifier.getComment());
								ll.addView(tv);
							}
							first = false;
							n++;
						}

						// day list
						ArrayList<WeekDayRange> days = r.getDays();
						if (days != null && days.size() > 0){
							for (WeekDayRange d:days) {
								addWeekDayRange(d, ll, r);
							}
						}
						// times
						ArrayList<TimeSpan> times = r.getTimes();
						if (times != null && times.size() > 0){
							StringBuffer b = new StringBuffer();
							for (TimeSpan ts:times) {
								b.append(ts.toString());
								if (times.get(times.size()-1)!=ts) {
									b.append(",");
								} 
							}
							TextView tv = new TextView(getActivity());
							tv.setText(b.toString());
							ll.addView(tv);
						}
					}
					Button deleteButton = new Button(getActivity());
					deleteButton.setText(R.string.delete_rule);
					deleteButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mergeableRules.remove(ruleList);
							updateRules(mergeableRules);
						}
					});
					ll.addView(deleteButton);

					Button addButton = new Button(getActivity());
					addButton.setText(R.string.add_rule);
					addButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mergeableRules.add(mergeableRules.indexOf(ruleList), new ArrayList<Rule>());
							updateRules(mergeableRules);
						}
					});
					ll.addView(addButton);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void addWeekDayRange(WeekDayRange d, LinearLayout ll, final Rule r) {
		ViewStub viewStub = new ViewStub(getActivity());
		viewStub.setId(R.id.weekdays_stub);
		ll.addView(viewStub);
		final WeekdaysDataSource weekdaysDataSource = new WeekdaysDataSource((AppCompatActivity) getActivity(), R.id.weekdays_stub);
		weekdaysDataSource.start(new WeekdaysDataSource.Callback() {
					@Override
					public void onWeekdaysItemClicked(int attachId, WeekdaysDataItem item) {

					}

					@Override
					public void onWeekdaysSelected(int attachId, ArrayList<WeekdaysDataItem> items) {
						weekdaysDataItemsToWeekDayRange(items, r);
					}
				});
	}

	private void weekdaysDataItemsToWeekDayRange(ArrayList<WeekdaysDataItem> items, Rule rule) {
		ArrayList<WeekDayRange> days = new ArrayList<>();
		for (WeekdaysDataItem item: items) {
			WeekDayRange dayRange = new WeekDayRange();
			if (item.isSelected()){
				dayRange.setStartDay(item.getLabel());
			}
			days.add(dayRange);
		}
		rule.setDays(days);
	}

	private void updateRules(ArrayList<ArrayList<Rule>> mergeableRules) {
		Log.d(OpeningHoursFragment.class.getSimpleName(), openingHoursValue);
		String newOpeningHours = mergeableRulesToString(mergeableRules);
		Log.d(OpeningHoursFragment.class.getSimpleName(), newOpeningHours);
		LinearLayout openingHoursLayout = (LinearLayout) getView().findViewById(R.id.openinghours_layout);
		buildForm(openingHoursLayout, newOpeningHours);
	}

	private String mergeableRulesToString(ArrayList<ArrayList<Rule>> mergeableRules) {
		StringBuilder b = new StringBuilder();
		for (ArrayList<Rule> ruleList: mergeableRules) {
			for (Rule r: ruleList) {
				b.append(r);
			}
			b.append(";");
		}
		b.delete(b.length() -2, b.length() - 1);
		return b.toString();
	}

	@SuppressLint("NewApi")
	NumberPicker getYearPicker(int year) {
    	NumberPicker np = new NumberPicker(getActivity());
		np.setMinValue(1900);
		np.setMaxValue(3000);
		np.setValue(year);
		np.setFormatter(new NumberPicker.Formatter() {
			@Override
			public String format(int value) {
				return String.format("%04d",value);    
			}
		});
		return np;
    }
 

	@Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	Log.d(DEBUG_TAG, "onSaveInstanceState");
    	// outState.putSerializable("PARENTS", savedParents);
    }
    
    
    @Override
    public void onPause() {
    	super.onPause();
    	Log.d(DEBUG_TAG, "onPause");
    	// savedParents  = getParentRelationMap();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	Log.d(DEBUG_TAG, "onStop");
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.d(DEBUG_TAG, "onDestroy");
    }
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		// disable address tagging for stuff that won't have an address
		// menu.findItem(R.id.tag_menu_address).setVisible(!type.equals(Way.NAME) || element.hasTagKey(Tags.KEY_BUILDING));
	}
}
