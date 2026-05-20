import { create } from 'zustand';

export type SoundTrack = 'rain' | 'lofi' | 'piano' | 'fire' | 'silent';

type SettingsState = {
  soundTrack: SoundTrack;
  volume: number;
  fullscreenFocus: boolean;
  notifications: boolean;
  vibration: boolean;
  setSoundTrack: (track: SoundTrack) => void;
  setVolume: (volume: number) => void;
  setFullscreenFocus: (value: boolean) => void;
  setNotifications: (value: boolean) => void;
  setVibration: (value: boolean) => void;
};

export const useSettingsStore = create<SettingsState>((set) => ({
  soundTrack: 'rain',
  volume: 60,
  fullscreenFocus: false,
  notifications: false,
  vibration: true,
  setSoundTrack: (soundTrack) => set({ soundTrack }),
  setVolume: (volume) => set({ volume }),
  setFullscreenFocus: (fullscreenFocus) => set({ fullscreenFocus }),
  setNotifications: (notifications) => set({ notifications }),
  setVibration: (vibration) => set({ vibration }),
}));
