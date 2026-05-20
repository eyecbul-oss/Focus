import React from 'react';
import { StyleSheet, Text, Pressable, View } from 'react-native';
import { COLORS } from '../../theme/colors';

const filters = ['Tümü', 'Kritik', 'Tamamlanan'];

export default function TaskFilters({ active, onChange }: { active: string; onChange: (v: string) => void }) {
  return (
    <View style={styles.row}>
      {filters.map((f) => (
        <Pressable key={f} onPress={() => onChange(f)} style={[styles.pill, active === f && styles.active]}>
          <Text style={[styles.text, active === f && styles.activeText]}>{f}</Text>
        </Pressable>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: 'row', gap: 8, marginBottom: 14 },
  pill: { paddingHorizontal: 14, minHeight: 40, borderRadius: 999, alignItems: 'center', justifyContent: 'center', backgroundColor: 'rgba(15,23,42,.70)', borderWidth: 1, borderColor: COLORS.border },
  active: { backgroundColor: COLORS.primary, borderColor: COLORS.primary },
  text: { color: COLORS.text, fontWeight: '900' },
  activeText: { color: '#111827' },
});
