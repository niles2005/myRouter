package com.xtwsoft.router.carrouter;

import com.xtwsoft.utils.EarthPos;

public class RoadEnd {
	public EarthPos m_ePos = null;
	
	public RoadEnd(EarthPos ePos) {
		this.m_ePos = ePos;
	}

	private CarLinker m_linker = null;
	public void addLinker(CarLinker linker) {
		linker.m_nextLinker = m_linker;
		m_linker = linker;
	}
	
	public int getLat() {
		return m_ePos.getILat();
	}
	
	public int getLon() {
		return m_ePos.getILon();
	}
	
	
	public CarLinker getLinker() {
		return m_linker;
	}
	
	public String getLatLonString() {
		return this.m_ePos.getLatLonString();
	}
	
	public boolean equals(RoadEnd carPos) {
		if(carPos != null && carPos.m_ePos != null) {
			if(m_ePos.getILat() == carPos.m_ePos.getILat() && m_ePos.getILon() == carPos.m_ePos.getILon()) {
				return true;
			}
		}
		return false;
	}
}
