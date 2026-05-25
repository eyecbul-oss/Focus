import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { COLORS } from '../theme/colors';
import GlassCard from '../components/GlassCard';
import { useFocusStore } from '../store/focusStore';

export default function CoachScreen() {
  const streak = useFocusStore((s) => s.streak);
  const sessions = useFocusStore((s) => s.sessions);

  const message =
    streak >= 7
      ? 'Harika gidiyorsun. Disiplin seviyen yükseldi.'
      : sessions >= 3
      ? 'İstikrar oluşuyor. Devam et.'
      : 'Bugün küçük bir adım bile önemli.';

  return (
    <View style={styles.container}>
      <Text style={styles.header}>AI Koç</Text>

      <GlassCard>
        <Text style={styles.message}>{message}</Text>
      </GlassCard>

      <GlassCard>
        <Text style={styles.label}>Streak</Text>
        <Text style={styles.value}>{streak} gün</Text>
      </GlassCard>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.bg,
    padding: 20,
  },
  header: {
    color: COLORS.text,
    fontSize: 28,
    fontWeight: '700',
    marginBottom: 20,
  },
  message: {
    color: COLORS.text,
    fontSize: 18,
    lineHeight: 28,
  },
  label: {
    color: COLORS.muted,
  },
  value: {
    color: COLORS.primary,
    fontSize: 40,
    fontWeight: '800',
    marginTop: 8,
  },
});
