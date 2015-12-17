package com.j2y.familypop.client;

import android.util.Log;

import java.util.ArrayList;

import com.j2y.network.base.FpNetConstants;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpcScenarioDirectorProxy
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpcScenarioDirectorProxy
{
	public static FpcScenarioDirectorProxy Instance;
	public ArrayList<FpcScenario_base> _scenarios = new ArrayList<FpcScenario_base>();
	public FpcScenario_base _activeScenario;
	public int _activeScenarioType;

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpcScenarioDirectorProxy()
	{
		Instance = this;
		_scenarios.add(new FpcScenario_talk());
		_scenarios.add(new FpcScenario_record());
		_scenarios.add(new FpcScenario_game());	
		
		_activeScenarioType = FpNetConstants.SCENARIO_NONE;
		_activeScenario = null;
	}	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void ChangeScenario(int scenarioType)
	{
        Log.i("[J2Y]", "[ChangeScenario] " + scenarioType);

		if(_activeScenarioType != FpNetConstants.SCENARIO_NONE && _activeScenario != null)
		{
			_activeScenario.OnDeactivated();
			_activeScenario = null;
		}
		
		_activeScenarioType = scenarioType;
		_activeScenario = _scenarios.get(_activeScenarioType);
		_activeScenario.OnActivated();
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpcScenario_base GetActiveScenario()
	{
		return _activeScenario;
	}
}
