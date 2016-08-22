/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  ViewPagerAndroid,
  TouchableNativeFeedback
} from 'react-native';
import MakeMojiTextInput from './MakeMojiRN/MakeMojiTextInput'
import TimerMixin from 'react-timer-mixin';

class MakeMojiReactNative extends Component {

  render() {
    return (
      <View style={styles.container} onPress={this.log}>
        <Text style={styles.welcome} onPress={() =>this.log('text')}>
          Welcome to React Native!
        </Text>
        <Text style={styles.instructions}>
          To get started, edit index.android.js
        </Text>
        <Text style={styles.instructions}>
          Double tap R on your keyboard to reload,{'\n'}
          Shake or press menu button for dev menu
        </Text>
        <MakeMojiTextInput style={styles.moji} onSendPressed={this.log} horizontal={true}/>

          <Text style={styles.instructions}>
              below4
          </Text>
      </View>
    );
  }
  log(s){
    console.log("logging s"+s);
  }
  componentDidMount(){
    //this.animationTimeout();
  }
  animationTimeout(){
    TimerMixin.setTimeout(
        () => {
         this.forceUpdate();
          this.animationTimeout();
           },
        50
    );

  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection:'column',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  moji:{
    height:100,
    alignSelf: 'stretch',
  }
});

AppRegistry.registerComponent('MakeMojiReactNative', () => MakeMojiReactNative);
