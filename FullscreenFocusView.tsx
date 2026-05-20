import React from 'react';
import { StyleSheet, View } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { COLORS } from '../../theme/colors';

export default function GradientBackground({children}:{children:React.ReactNode}) {
  return (
    <LinearGradient colors={[COLORS.bg, COLORS.bg2, '#101827']} style={styles.root}>
      <View style={styles.goldGlow}/>
      <View style={styles.cyanGlow}/>
      {children}
    </LinearGradient>
  );
}
const styles = StyleSheet.create({
  root:{flex:1},
  goldGlow:{position:'absolute',top:-80,left:-60,width:260,height:260,borderRadius:260,backgroundColor:'rgba(250,204,21,0.16)'},
  cyanGlow:{position:'absolute',top:80,right:-90,width:260,height:260,borderRadius:260,backgroundColor:'rgba(56,189,248,0.12)'}
});
