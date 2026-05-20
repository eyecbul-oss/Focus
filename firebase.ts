import React from 'react';
import { StyleSheet } from 'react-native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';
import HomeScreen from '../screens/Home/HomeScreen';
import FocusScreen from '../screens/Focus/FocusScreen';
import TasksScreen from '../screens/Tasks/TasksScreen';
import StatsScreen from '../screens/Stats/StatsScreen';
import ExamScreen from '../screens/Exam/ExamScreen';
import { COLORS } from '../theme/colors';

const Tab=createBottomTabNavigator();
const icons:any={Koç:'sparkles',Görev:'checkbox',Sayaç:'timer',Sınav:'calendar',Ritim:'stats-chart'};

export default function BottomTabs(){
  return <Tab.Navigator screenOptions={({route})=>({
    headerShown:false, tabBarStyle:styles.bar, tabBarActiveTintColor:COLORS.primary, tabBarInactiveTintColor:COLORS.muted,
    tabBarLabelStyle:styles.label, tabBarIcon:({color,size})=><Ionicons name={icons[route.name]} color={color} size={size}/>
  })}>
    <Tab.Screen name="Koç" component={HomeScreen}/>
    <Tab.Screen name="Görev" component={TasksScreen}/>
    <Tab.Screen name="Sayaç" component={FocusScreen}/>
    <Tab.Screen name="Sınav" component={ExamScreen}/>
    <Tab.Screen name="Ritim" component={StatsScreen}/>
  </Tab.Navigator>;
}
const styles=StyleSheet.create({
  bar:{position:'absolute',left:14,right:14,bottom:12,height:76,borderRadius:28,backgroundColor:'rgba(2,6,23,.92)',borderTopWidth:0,borderWidth:1,borderColor:'rgba(255,255,255,.12)',elevation:16},
  label:{fontSize:11,fontWeight:'900'}
});
