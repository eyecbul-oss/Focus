import { Audio } from 'expo-av';

let sound: Audio.Sound | null = null;

export async function stopSound() {
  if (sound) {
    await sound.stopAsync().catch(() => {});
    await sound.unloadAsync().catch(() => {});
    sound = null;
  }
}

export async function playTrack(track: string) {
  await stopSound();

  if (track === 'silent') return;

  // Dosyalar eklenince buraya require ile bağlanacak.
  // Şimdilik güvenli stub: uygulama hata vermesin.
  return;
}
