package it.itskennedy.tsaim.geoad.interfaces;

import it.itskennedy.tsaim.geoad.entity.LocationModel;

import java.util.ArrayList;

public interface ILocationsList
{
	void notifyLocationsListChanged(ArrayList<LocationModel> aLocationsList);
}
