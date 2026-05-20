// 🔊 Ses Servisi - Focus Uygulaması
import { Audio } from 'expo-av';

type SoundType = 'complete' | 'break' | 'warning' | 'tick';

const SOUND_URLS = {
  complete: require('../assets/sounds/complete.wav'),
  break: require('../assets/sounds/break.wav'),
  warning: require('../assets/sounds/warning.wav'),
  tick: require('../assets/sounds/tick.wav'),
};

class AudioService {
  private sounds: Map<SoundType, Audio.Sound> = new Map();
  private isInitialized = false;

  async initialize() {
    if (this.isInitialized) return;
    try {
      await Audio.setAudioModeAsync({
        allowsRecordingIOS: false,
        playsInSilentModeIOS: true,
        staysActiveInBackground: true,
      });
      this.isInitialized = true;
    } catch (e) {
      console.warn('Ses başlatılamadı:', e);
    }
  }

  async playSound(type: SoundType, volume: number = 1.0) {
    try {
      await this.initialize();

      // Mevcut sesi veya yenisini oluştur
      let sound = this.sounds.get(type);
      if (!sound) {
        const { sound: newSound } = await Audio.Sound.createAsync(SOUND_URLS[type]);
        sound = newSound;
        this.sounds.set(type, sound);
      }

      await sound.setVolumeAsync(volume);
      await sound.playAsync();
    } catch (e) {
      console.warn(`Ses oynatılamadı (${type}):`, e);
    }
  }

  async playCompletionSound(volume?: number) {
    await this.playSound('complete', volume);
  }

  async playBreakSound(volume?: number) {
    await this.playSound('break', volume);
  }

  async playWarningSound(volume?: number) {
    await this.playSound('warning', volume);
  }

  async playTickSound(volume?: number) {
    await this.playSound('tick', volume ?? 0.3);
  }

  async cleanup() {
    for (const [_, sound] of this.sounds) {
      try {
        await sound.unloadAsync();
      } catch (e) {
        // Ignore cleanup errors
      }
    }
    this.sounds.clear();
  }
}

export const audioService = new AudioService();
