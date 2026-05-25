import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, ScrollView } from 'react-native';
import { COLORS } from '../theme/colors';
import GlassCard from '../components/GlassCard';
import { useFocusStore } from '../store/focusStore';

export default function ExamScreen() {
  const [name, setName] = useState('');
  const [date, setDate] = useState('');

  const exams = useFocusStore((s) => s.exams);
  const addExam = useFocusStore((s) => s.addExam);

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.header}>Sınav Takibi</Text>

      <TextInput
        placeholder='Sınav adı'
        placeholderTextColor={COLORS.muted}
        value={name}
        onChangeText={setName}
        style={styles.input}
      />

      <TextInput
        placeholder='2026-06-15'
        placeholderTextColor={COLORS.muted}
        value={date}
        onChangeText={setDate}
        style={styles.input}
      />

      <TouchableOpacity
        style={styles.button}
        onPress={() => {
          addExam(name, date);
          setName('');
          setDate('');
        }}
      >
        <Text style={styles.buttonText}>Sınav Ekle</Text>
      </TouchableOpacity>

      {exams.map((exam) => (
        <GlassCard key={exam.id}>
          <Text style={styles.exam}>{exam.name}</Text>
          <Text style={styles.date}>{exam.date}</Text>
        </GlassCard>
      ))}
    </ScrollView>
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
  input: {
    backgroundColor: COLORS.cardSolid,
    color: COLORS.text,
    borderRadius: 16,
    padding: 16,
    marginBottom: 12,
  },
  button: {
    backgroundColor: COLORS.primary,
    borderRadius: 16,
    padding: 16,
    marginBottom: 18,
  },
  buttonText: {
    textAlign: 'center',
    fontWeight: '700',
  },
  exam: {
    color: COLORS.text,
    fontSize: 18,
    fontWeight: '700',
  },
  date: {
    color: COLORS.muted,
    marginTop: 8,
  },
});
