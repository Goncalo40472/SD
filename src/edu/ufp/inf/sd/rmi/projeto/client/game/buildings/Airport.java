package edu.ufp.inf.sd.rmi.projeto.client.game.buildings;

public class Airport extends Base {

	public Airport(int owner, int xx, int yy) {
		super(owner, xx, yy);
		name="Airport";
		desc="Creates Air units.";
		img = 3;
		Menu = "airport";
	}
}
