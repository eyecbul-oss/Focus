import React, { useState } from 'react';
import { ScrollView, StyleSheet, Text, View } from 'react-native';
import * as Haptics from 'expo-haptics';
import GradientBackground from '../../components/ui/GradientBackground';
import GlassCard from '../../components/ui/GlassCard';
import NeonButton from '../../components/ui/NeonButton';
import TimerRing from '../../components/timer/TimerRing';
import BreakModal from '../../components/timer/BreakModal';
import FullscreenFocusView from './FullscreenFocusView';
import useFocusTimer from '../../hooks/useFocusTimer';
import { COLORS } from '../../theme/colors';
import { useFocusStore } from '../../store/focusStore';
import { useSettingsStore, SoundTrack } from '../../store/settingsStore';

const modes = [
  { label: 'Quick', seconds: 10 * 60 },
  { label: 'Standart', seconds: 25 * 60 },
  { label: 'Deep', seconds: 60 * 60 },
  { label: 'Deneme', seconds: 120 * 60 },
];

const sounds: { label: string; value: SoundTrack }[] = [
  { label: 'Yağmur', value: 'rain' },
  { label: 'Lo-fi', value: 'lofi' },
  { label: 'Piano', value: 'piano' },
  { label: 'Ateş', value: 'fire' },
  { label: 'Sessiz', value: 'silent' },
];

export default function FocusScreen() {
  const focusSeconds = useFocusStore((s) => s.focusSeconds);
  const setFocusSeconds = useFocusStore((s) => s.setFocusSeconds);
  const breakSeconds = useFocusStore((s) => s.breakSeconds);
  const soundTrack = useSettingsStore((s) => s.soundTrack);
  const setSoundTrack = useSettingsStore((s) => s.setSoundTrack);
  const fullscreen = useSettingsStore((s) => s.fullscreenFocus);
  const setFullscreen = useSettingsStore((s) => s.setFullscreenFocus);

  const timer = useFocusTimer(focusSeconds);
  const breakTimer = useFocusTimer(breakSeconds);
  const [breakVisible, setBreakVisible] = useState(false);

  const openBreak = () => {
    timer.pause();
    setBreakVisible(true);
  };

  const finishBreak = () => {
    breakTimer.pause();
    setBreakVisible(false);
  };

  return (
    <GradientBackground>
      <ScrollView contentContainerStyle={[styles.container, fullscreen && styles.fullscreen]}>
        <Text style={styles.title}>{fullscreen ? 'Tam Ekran Focus' : 'Focus'}</Text>
        <TimerRing remaining={timer.remaining} />

        <View style={styles.actions}>
          <NeonButton title={timer.running ? 'Duraklat' : 'Başlat'} onPress={() => { Haptics.selectionAsync(); timer.running ? timer.pause() : timer.start(); }} />
          <NeonButton title="Mola" variant="dark" onPress={openBreak} />
          <NeonButton title="Sıfırla" variant="dark" onPress={timer.reset} />
        </View>

        <GlassCard style={styles.card}>
          <Text style={styles.section}>Focus Modları</Text>
          <View style={styles.wrap}>
            {modes.map((m) => (
              <NeonButton key={m.label} title={m.label} variant={m.seconds === focusSeconds ? 'primary' : 'dark'} onPress={() => setFocusSeconds(m.seconds)} style={styles.smallBtn} />
            ))}
          </View>
        </GlassCard>

        <GlassCard style={styles.card}>
          <Text style={styles.section}>Müzik Seçimi</Text>
          <View style={styles.wrap}>
            {sounds.map((s) => (
              <NeonButton key={s.value} title={s.label} variant={s.value === soundTrack ? 'primary' : 'dark'} onPress={() => setSoundTrack(s.value)} style={styles.smallBtn} />
            ))}
          </View>
        </GlassCard>

        <GlassCard>
          <Text style={styles.section}>Tam Ekran</Text>
          <Text style={styles.text}>Odak sırasında dikkat dağıtıcı kartları azaltır.</Text>
          <NeonButton title={fullscreen ? 'Standart Görünüm' : 'Tam Ekran Mod'} variant={fullscreen ? 'danger' : 'primary'} onPress={() => setFullscreen(!fullscreen)} />
        </GlassCard>

        <FullscreenFocusView visible={fullscreen} remaining={timer.remaining} running={timer.running} onToggle={() => timer.running ? timer.pause() : timer.start()} onExit={() => setFullscreen(false)} />
        <BreakModal visible={breakVisible} remaining={breakTimer.remaining} running={breakTimer.running} onToggle={() => breakTimer.running ? breakTimer.pause() : breakTimer.start()} onFinish={finishBreak} />
      </ScrollView>
    </GradientBackground>
  );
}

const styles = StyleSheet.create({
  container: { padding: 18, paddingBottom: 110 },
  fullscreen: { justifyContent: 'center', minHeight: 760 },
  title: { color: COLORS.text, fontSize: 32, fontWeight: '900', marginTop: 18, marginBottom: 24 },
  actions: { flexDirection: 'row', gap: 10, marginVertical: 22 },
  card: { marginBottom: 14 },
  section: { color: COLORS.primary, fontSize: 18, fontWeight: '900', marginBottom: 12 },
  wrap: { flexDirection: 'row', flexWrap: 'wrap', gap: 10 },
  smallBtn: { minWidth: '45%' },
  text: { color: COLORS.muted, fontWeight: '800', lineHeight: 22, marginBottom: 12 },
});
