import { Audio } from 'expo-av';
import * as Haptics from 'expo-haptics';

let sound: Audio.Sound | null = null;

const TRACKS: Record<string, any> = {
  // rain: require('../../assets/sounds/rain.mp3'),
  // lofi: require('../../assets/sounds/lofi.mp3'),
  // piano: require('../../assets/sounds/piano.mp3'),
  // fire: require('../../assets/sounds/fire.mp3'),
};

export async function stopSound() {
  if (sound) {
    await sound.stopAsync().catch(() => {});
    await sound.unloadAsync().catch(() => {});
    sound = null;
  }
}

export async function playTrack(track: string, volume = 0.6) {
  await stopSound();
  if (track === 'silent' || !TRACKS[track]) return;

  const result = await Audio.Sound.createAsync(TRACKS[track], {
    isLooping: true,
    volume,
    shouldPlay: true,
  });

  sound = result.sound;
}

export async function vibrate(type: 'light' | 'medium' | 'heavy' = 'light') {
  const map = {
    light: Haptics.ImpactFeedbackStyle.Light,
    medium: Haptics.ImpactFeedbackStyle.Medium,
    heavy: Haptics.ImpactFeedbackStyle.Heavy,
  };
  await Haptics.impactAsync(map[type]).catch(() => {});
}
