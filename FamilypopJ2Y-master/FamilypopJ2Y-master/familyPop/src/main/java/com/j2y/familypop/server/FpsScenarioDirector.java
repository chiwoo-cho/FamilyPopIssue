package com.j2y.familypop.server;

import java.util.ArrayList;

import com.j2y.network.base.FpNetConstants;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// FpsScenarioDirector
//
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public class FpsScenarioDirector
{
	public static FpsScenarioDirector Instance;
	public ArrayList<FpsScenario_base> _scenarios = new ArrayList<FpsScenario_base>();
	private FpsScenario_base _activeScenario;
	private int _activeScenarioType;

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpsScenarioDirector()
	{
		Instance = this;
		_scenarios.add(new FpsScenario_talk());
		_scenarios.add(new FpsScenario_record());
		_scenarios.add(new FpsScenario_game());	
		
		_activeScenarioType = FpNetConstants.SCENARIO_NONE;
		_activeScenario = null;
	}		

	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void ChangeScenario(int scenarioType)
	{	
		if(_activeScenarioType != FpNetConstants.SCENARIO_NONE && _activeScenario != null)
		{
			_activeScenario.OnDeactivated();
			_activeScenario = null;
		}
		
		_activeScenarioType = scenarioType;
        _activeScenario = (scenarioType == FpNetConstants.SCENARIO_NONE)
            ? null : _scenarios.get(_activeScenarioType);
        if(_activeScenario != null)
		    _activeScenario.OnActivated();
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public int GetActiveScenarioType()
	{
		return _activeScenarioType;
	}
		
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	public FpsScenario_base GetActiveScenario()
	{
		return _activeScenario;
	}



    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public void CloseServer()
    {
        ChangeScenario(FpNetConstants.SCENARIO_NONE);
    }

}
