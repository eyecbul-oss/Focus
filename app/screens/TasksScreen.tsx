import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, ScrollView } from 'react-native';
import { useFocusStore } from '../store/focusStore';
import { COLORS } from '../theme/colors';
import GlassCard from '../components/GlassCard';

export default function TasksScreen() {
  const [title, setTitle] = useState('');
  const tasks = useFocusStore((s) => s.tasks);
  const addTask = useFocusStore((s) => s.addTask);
  const toggleTask = useFocusStore((s) => s.toggleTask);

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.header}>Görevler</Text>

      <TextInput
        value={title}
        onChangeText={setTitle}
        placeholder='Yeni görev'
        placeholderTextColor={COLORS.muted}
        style={styles.input}
      />

      <TouchableOpacity
        style={styles.button}
        onPress={() => {
          addTask(title);
          setTitle('');
        }}
      >
        <Text style={styles.buttonText}>Görev Ekle</Text>
      </TouchableOpacity>

      {tasks.map((task) => (
        <GlassCard key={task.id}>
          <TouchableOpacity onPress={() => toggleTask(task.id)}>
            <Text style={[styles.task, task.done && styles.done]}>
              {task.done ? '✓ ' : '○ '} {task.title}
            </Text>
          </TouchableOpacity>
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
  },
  button: {
    backgroundColor: COLORS.primary,
    borderRadius: 16,
    padding: 16,
    marginTop: 12,
    marginBottom: 18,
  },
  buttonText: {
    textAlign: 'center',
    fontWeight: '700',
  },
  task: {
    color: COLORS.text,
    fontSize: 16,
  },
  done: {
    textDecorationLine: 'line-through',
    color: COLORS.muted,
  },
});
