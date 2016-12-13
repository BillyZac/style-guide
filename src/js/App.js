import React from 'react'
import Code from './components/Code.js'

const Text = require('whippersnapper/build/Text.js')
import Header from 'whippersnapper/build/Header';
const Footer = require('whippersnapper/build/Footer.js')
require('zzzss/dist/css/zzzss.css')
require('../style.css')

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

        <Footer appVersion="1.0" />
      </div>
    )
  }
}

export default App
