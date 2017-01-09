import React from 'react'
import Component from './components/Component.js'
import CommitList from './components/CommitList.js'
import Header from 'whippersnapper/build/Header'
const Footer = require('whippersnapper/build/Footer.js')
require('zzzss/dist/css/zzzss.css')
require('../style.css')
import fetch from 'isomorphic-fetch'

import components from './data/components.js'

class App extends React.Component {
  constructor(props) {
    super(props)
    this.state = {commitList: []}
  }

  componentDidMount() {
    fetch('./temp.json')
      .then(response => {
        console.log(response);
        return response.json()
      })
      .then(commitList => {
        this.setState({
          commitList: commitList
        })
      })
  }

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
        { components.map((component, i) => (
          <Component
            key={`component-${i}`}
            title={component.title}
            component={component.component}
            codeSnippet={component.codeSnippet}
          />
        ))}
        <CommitList list={ this.state.commitList }/>
        <Footer appVersion="1.0" />
      </div>
    )
  }
}

export default App
