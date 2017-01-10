import React from 'react'
import Component from './components/Component.js'
import CommitList from './components/CommitList.js'
import Header from 'whippersnapper/build/Header'
const Footer = require('whippersnapper/build/Footer.js')
require('zzzss/dist/css/zzzss.css')
require('../style.css')
import fetch from 'isomorphic-fetch'

const GIT_COMMIT_URL = 'https://api.github.com/repos/buildit/zzzss/commits'

import components from './data/components.js'

class App extends React.Component {
  constructor(props) {
    super(props)
    this.state = {commitList: []}
  }

  componentDidMount() {
    fetch(GIT_COMMIT_URL)
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
    const main = components.map((component, i) => (
      <Component
        key={`component-${i}`}
        title={component.title}
        component={component.component}
        codeSnippet={component.codeSnippet}
      />
    ))

    const description =
      (<div className="intro">
        <p>Demonstrating the Whippersnapper React component library with the zzzss style library.</p>
      </div>)

    return (
      <div>
        <Header
          logotype="Living Style Guide"
          onLogoClick={() => console.log('clik!')}
        />
        {description}
        {main}
        <CommitList list={ this.state.commitList }/>
        <Footer appVersion="1.0" />
      </div>
    )
  }
}

export default App
