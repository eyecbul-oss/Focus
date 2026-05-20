import React from 'react';
import { StyleSheet } from 'react-native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';
import HomeScreen from '../screens/Home/HomeScreen';
import FocusScreen from '../screens/Focus/FocusScreen';
import TasksScreen from '../screens/Tasks/TasksScreen';
import StatsScreen from '../screens/Stats/StatsScreen';
import ExamScreen from '../screens/Exam/ExamScreen';
import NotesScreen from '../screens/Notes/NotesScreen';
import ProfileScreen from '../screens/Profile/ProfileScreen';
import { COLORS } from '../theme/colors';

const Tab = createBottomTabNavigator();

const icons: Record<string, keyof typeof Ionicons.glyphMap> = {
  Koç: 'sparkles',
  Görev: 'checkbox',
  Sayaç: 'timer',
  Sınav: 'calendar',
  Ritim: 'stats-chart',
  Not: 'create',
  Profil: 'person',
};

export default function BottomTabs() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarStyle: styles.bar,
        tabBarActiveTintColor: COLORS.primary,
        tabBarInactiveTintColor: COLORS.muted,
        tabBarLabelStyle: styles.label,
        tabBarIcon: ({ color, size }) => <Ionicons name={icons[route.name]} color={color} size={size} />,
      })}
    >
      <Tab.Screen name="Koç" component={HomeScreen} />
      <Tab.Screen name="Görev" component={TasksScreen} />
      <Tab.Screen name="Sayaç" component={FocusScreen} />
      <Tab.Screen name="Sınav" component={ExamScreen} />
      <Tab.Screen name="Ritim" component={StatsScreen} />
      <Tab.Screen name="Not" component={NotesScreen} />
      <Tab.Screen name="Profil" component={ProfileScreen} />
    </Tab.Navigator>
  );
}

const styles = StyleSheet.create({
  bar: { position: 'absolute', left: 8, right: 8, bottom: 8, height: 78, borderRadius: 28, backgroundColor: 'rgba(2,6,23,0.94)', borderTopWidth: 0, borderWidth: 1, borderColor: COLORS.border, elevation: 16 },
  label: { fontSize: 9, fontWeight: '900', marginBottom: 4 },
});
