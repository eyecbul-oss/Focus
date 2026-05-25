import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { useFocusStore } from '../store/focusStore';
import GlassCard from '../components/GlassCard';
import { COLORS } from '../theme/colors';

export default function StatsScreen() {
  const totalMinutes = useFocusStore((s) => s.totalMinutes);
  const sessions = useFocusStore((s) => s.sessions);

  return (
    <View style={styles.container}>
      <Text style={styles.header}>İstatistikler</Text>

      <GlassCard>
        <Text style={styles.label}>Toplam Dakika</Text>
        <Text style={styles.value}>{totalMinutes}</Text>
      </GlassCard>

      <GlassCard>
        <Text style={styles.label}>Toplam Seans</Text>
        <Text style={styles.value}>{sessions}</Text>
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
  label: {
    color: COLORS.muted,
    marginBottom: 8,
  },
  value: {
    color: COLORS.text,
    fontSize: 34,
    fontWeight: '800',
  },
});
