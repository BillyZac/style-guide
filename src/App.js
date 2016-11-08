import React from 'react'

const Text = require('whippersnapper/build/Text.js')
import Header from 'whippersnapper/build/Header';
const Footer = require('whippersnapper/build/Footer.js')
require('zzzss/dist/css/zzzss.css')
require('./style.css')

const Code = React.createClass({
  getInitialState: function() {
      return { isCodeSnippetVisible: false }
  },

  render: function() {
    const { codeSnippet } = this.props;
    const codeSnippetClass =
      this.state.isCodeSnippetVisible ? `code-snippet` : `code-snippet hidden`
    return (
      <div>
        <span
          className="code"
          onClick={ () => {
            this.setState({ isCodeSnippetVisible: !this.state.isCodeSnippetVisible })
          }}
          >code</span>
        <div
            className={codeSnippetClass}
          >{codeSnippet}</div>
        </div>
    )
  }
})

class App extends React.Component {
  render() {
    return (
      <div>
        <Header
          logotype="Living Style Guide"
          onLogoClick={() => console.log('clik!')}
        />
        <div className="intro">
          <p>Demonstrating the Whippersnapper React component library with the zzzss style library.</p>
        </div>
        <h2>Text component</h2>
        <Text
          label="Planet"
          content="Jupiter"
        />
        <Code codeSnippet='<Text label="Planet" content="Jupiter" />' />
        <Footer
          appVersion="1.0"
        />
      </div>
    )
  }
}

export default App
