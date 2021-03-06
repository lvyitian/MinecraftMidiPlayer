package com.khjxiaogu.MCMidi.Midi;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.block.Block;
import org.bukkit.block.NoteBlock;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.khjxiaogu.MCMidi.MCMidi;

public class NoteInfo implements ConfigurationSerializable {
	@FunctionalInterface
	interface Initializer {
		public void init(NoteInfo n, int key);
	}

	public long ticks;
	public Note n;
	public Instrument ins;
	public int volume = 64;
	private final static Instrument[] inss = new Instrument[25];
	private final static Note[] notes = new Note[25];
	private static Initializer init;
	private int key;

	public static void initNotes() {
		if (MCMidi.plugin.getConfig().getBoolean("universal", false)) {
			for (int i = 24; i >= 8; i--) {
				NoteInfo.inss[i] = Instrument.SNARE_DRUM;
			}
			Instrument Bell;
			try {
				Bell = Instrument.valueOf("BELL");// attempt to use 1.12 bell to provide better performation
			} catch (Throwable t) {
				Bell = Instrument.SNARE_DRUM;
			}
			NoteInfo.inss[7] = Bell;
			NoteInfo.inss[6] = Bell;
			NoteInfo.inss[5] = Instrument.PIANO;
			NoteInfo.inss[4] = Instrument.PIANO;
			NoteInfo.inss[3] = Instrument.BASS_GUITAR;
			NoteInfo.inss[2] = Instrument.BASS_GUITAR;
			NoteInfo.inss[1] = Instrument.BASS_DRUM;
			NoteInfo.inss[0] = Instrument.BASS_DRUM;
			NoteInfo.init = (t, k) -> {
				k += 6;
				t.n = NoteInfo.notes[k % 24];
				t.ins = NoteInfo.inss[k / 12];
			};
		} else {
			for (int i = 24; i >= 10; i--) {
				NoteInfo.inss[i] = Instrument.BASS_DRUM;
			}

			NoteInfo.inss[9] = Instrument.BASS_DRUM;
			NoteInfo.inss[8] = Instrument.BASS_DRUM;
			NoteInfo.inss[7] = Instrument.BASS_DRUM;
			NoteInfo.inss[6] = Instrument.STICKS;
			NoteInfo.inss[5] = Instrument.PIANO;
			NoteInfo.inss[4] = Instrument.SNARE_DRUM;
			NoteInfo.inss[3] = Instrument.BASS_GUITAR;
			NoteInfo.inss[2] = Instrument.BASS_GUITAR;
			NoteInfo.inss[1] = Instrument.BASS_GUITAR;
			NoteInfo.inss[0] = Instrument.BASS_GUITAR;
			NoteInfo.init = (t, k) -> {
				t.n = NoteInfo.notes[k % 12 + 6];
				t.ins = NoteInfo.inss[k / 12];
			};
		}
		NoteInfo.notes[0] = Note.sharp(0, Tone.F);
		NoteInfo.notes[1] = Note.natural(0, Tone.G);
		NoteInfo.notes[2] = Note.sharp(0, Tone.G);
		NoteInfo.notes[3] = Note.natural(0, Tone.A);
		NoteInfo.notes[4] = Note.sharp(0, Tone.A);
		NoteInfo.notes[5] = Note.natural(0, Tone.B);
		NoteInfo.notes[6] = Note.natural(0, Tone.C);
		NoteInfo.notes[7] = Note.sharp(0, Tone.C);
		NoteInfo.notes[8] = Note.natural(0, Tone.D);
		NoteInfo.notes[9] = Note.sharp(0, Tone.D);
		NoteInfo.notes[10] = Note.natural(0, Tone.E);
		NoteInfo.notes[11] = Note.natural(0, Tone.F);
		NoteInfo.notes[12] = Note.sharp(1, Tone.F);
		NoteInfo.notes[13] = Note.natural(1, Tone.G);
		NoteInfo.notes[14] = Note.sharp(1, Tone.G);
		NoteInfo.notes[15] = Note.natural(1, Tone.A);
		NoteInfo.notes[16] = Note.sharp(1, Tone.A);
		NoteInfo.notes[17] = Note.natural(1, Tone.B);
		NoteInfo.notes[18] = Note.natural(1, Tone.C);
		NoteInfo.notes[19] = Note.sharp(1, Tone.C);
		NoteInfo.notes[20] = Note.natural(1, Tone.D);
		NoteInfo.notes[21] = Note.sharp(1, Tone.D);
		NoteInfo.notes[22] = Note.natural(1, Tone.E);
		NoteInfo.notes[23] = Note.natural(1, Tone.F);
		NoteInfo.notes[24] = Note.sharp(2, Tone.F);
	}

	public NoteInfo(long ticks) {
		this.ticks = ticks;
		n = null;
		key = 0;
	}

	public NoteInfo(int key, long tick, int vol) {
		volume = vol;
		ticks = tick;
		NoteInfo.init.init(this, key);
		this.key = key;
	}

	public static NoteInfo valueOf(Map<String,Object> map) {
		if(map.containsKey("x")) {
			String ns=(String) map.get("x");
			String[] cs=ns.split(":");
			int k=Integer.parseInt(cs[0]);
			int t=Integer.parseInt(cs[1]);
			int v=Integer.parseInt(cs[2]);
			return getNote(k,t,v);
		}
		return getNote((int) map.get("k"), (int) map.get("t"), (int) map.get("v"));
	}

	public static NoteInfo getNote(int key, long tick, int vol) {
		return new NoteInfo(key, tick, vol);
	}

	public void play(Player p) {
		if (n != null) {
			p.playNote(p.getLocation(), ins, n);
		}
	}

	public void play(NoteBlock b) {
		if (n != null) {
			b.play(ins, n);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("x",key+":"+ticks+":"+volume);
		return map;
	}
	public void placeBlock(Location l, Material base) {
		Block b = l.getWorld().getBlockAt(l);
		Block under = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY() - 1, l.getBlockZ());
		l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY() - 2, l.getBlockZ()).setType(base);
		switch (ins) {
		case BASS_DRUM:
			under.setType(Material.STONE);
			break;
		case STICKS:
			under.setType(Material.GLASS);
			break;
		case PIANO:
			under.setType(Material.IRON_BLOCK);
			break;
		case SNARE_DRUM:
			under.setType(Material.SAND);
			break;
		case BASS_GUITAR:
			under.setType(Material.WOOD);
			break;
		default:
			under.setType(Material.GOLD_BLOCK);
			break;
		}
		b.setType(Material.NOTE_BLOCK);
		NoteBlock nb = (NoteBlock) b.getState();
		nb.setNote(n);
		nb.update();
		// b.setData(n.getId());
	}
}