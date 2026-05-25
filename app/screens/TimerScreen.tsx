import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import * as Haptics from 'expo-haptics';
import GlassCard from '../components/GlassCard';
import NeonButton from '../components/NeonButton';
import useFocusTimer from '../hooks/useFocusTimer';
import { COLORS } from '../theme/colors';
import { useFocusStore } from '../store/focusStore';
import { scheduleFocusFinishedNotification } from '../services/notificationService';

export default function TimerScreen() {
  const timer = useFocusTimer(25);
  const addSession = useFocusStore((s) => s.addSession);

  async function startTimer() {
    await Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium);
    await scheduleFocusFinishedNotification(timer.totalMinutes);
    timer.setRunning(true);
  }

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Focus Timer</Text>

      <GlassCard>
        <Text style={styles.time}>{timer.time}</Text>

        <NeonButton
          title={timer.running ? 'Durdur' : 'Baslat'}
          onPress={() => {
            if (timer.running) {
              timer.setRunning(false);
            } else {
              startTimer();
            }
          }}
        />

        <NeonButton
          title='Seansi Kaydet'
          onPress={() => {
            addSession(timer.totalMinutes);
            Haptics.notificationAsync(Haptics.NotificationFeedbackType.Success);
          }}
        />
      </GlassCard>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.bg,
    padding: 20,
    justifyContent: 'center',
  },
  header: {
    color: COLORS.text,
    fontSize: 28,
    fontWeight: '700',
    marginBottom: 20,
  },
  time: {
    color: COLORS.text,
    fontSize: 56,
    textAlign: 'center',
    fontWeight: '800',
    marginBottom: 20,
  },
});
