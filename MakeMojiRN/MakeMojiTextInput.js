import { PropTypes } from 'react';
import React, { Component } from 'react';
import { requireNativeComponent, View } from 'react-native';

class MakeMojiTextInput extends React.Component {
    constructor(props) {
        super(props);
        this.onSendPressed = this._onSendPressed.bind(this);
        this.onCameraPressed = this._onCameraPressed.bind(this);
    }
    _onSendPressed(event: Event) {
        if (!this.props.onSendPressed) {
            return;
        }
        this.props.onSendPressed(event.nativeEvent.message);
    }
    _onCameraPressed(event: Event) {
    if (!this.props.onCameraPressed) {
        return;
    }
    this.props.onCameraPressed(event.nativeEvent.message);
}
    render() {
        return <RCTMojiInputLayout {...this.props} onSendPressed={this._onSendPressed} onCameraPressed={this._onCameraPressed} />;
    }
    shouldComponentUpdate(nextProps,nextState){
        return true;
    }
}
MakeMojiTextInput.propTypes = {
    ...View.propTypes,
    onSendPressed: React.PropTypes.func,
    onCameraPressed: React.PropTypes.func
};

module.exports = requireNativeComponent(`RCTMojiInputLayout`, MakeMojiTextInput);