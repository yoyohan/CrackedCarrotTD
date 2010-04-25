package com.crackedcarrot.multiplayer;

import com.crackedcarrot.GameLoop;
import com.crackedcarrot.GameLoopGUI;
import com.crackedcarrot.NativeRender;
import com.crackedcarrot.Player;
import com.crackedcarrot.SoundManager;
import com.crackedcarrot.Tower;
import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.fileloader.Map;

public class MultiplayerGameLoop extends GameLoop {

	public MultiplayerGameLoop(NativeRender renderHandle, Map gameMap,
			Level[] waveList, Tower[] tTypes, Player p, GameLoopGUI gui,
			SoundManager sm) {
		super(renderHandle, gameMap, waveList, tTypes, p, gui, sm);
	}
	


}
