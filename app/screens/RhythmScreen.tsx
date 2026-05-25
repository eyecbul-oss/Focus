import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { COLORS } from '../theme/colors';
import GlassCard from '../components/GlassCard';
import { useFocusStore } from '../store/focusStore';

export default function RhythmScreen() {
  const totalMinutes = useFocusStore((s) => s.totalMinutes);
  const sessions = useFocusStore((s) => s.sessions);
  const average = sessions > 0 ? Math.round(totalMinutes / sessions) : 0;

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Calisma Ritmi</Text>

      <GlassCard>
        <Text style={styles.label}>Ortalama Seans</Text>
        <Text style={styles.value}>{average} dk</Text>
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
  },
  value: {
    color: COLORS.primary,
    fontSize: 38,
    fontWeight: '800',
    marginTop: 8,
  },
});
