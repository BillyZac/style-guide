# Living Style guide

[As Brad Frost says](http://atomicdesign.bradfrost.com/chapter-4/#pitching-patterns), "Centralizing UI components in a pattern library establishes a shared vocabulary for everyone in the organization, and creates a more collaborative workflow across all disciplines." That's what this style guide is all about.

This Style Guide demonstrates the styles in [zzzss](https://www.npmjs.com/package/zzzss) and uses the React components in [Whippersnapper](https://www.npmjs.com/package/whippersnapper).

## To run the style guide locally
Install dependencies:
```
npm install
```

Start the dev server:
```
npm start
```
This runs Webpack Dev Server and serves the app at http://localhost:3333/. Make changes in the code and see the app automatically reload. Magical!


## To build for deployment
```
npm run build
```

And deploy 'build' however you like.

## Continuous deployment
The Jenkins pipeline is set up to deploy to a site accessible on Buildit's VPN. If you're on the VPN, check it out [here](http://style-guide.riglet/).

The Jenkins job can be monitored [here](http://jenkins.riglet:9000/jenkins/job/style-guide-staging-pipeline/).
