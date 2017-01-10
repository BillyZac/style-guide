const path = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const CleanWebpackPlugin = require('clean-webpack-plugin')
const ExtractTextPlugin = require('extract-text-webpack-plugin')

const buildFolder = 'build'

module.exports = {
  entry: [
    './src/js/main.js',
    './src/scss/main.scss'
  ],
  output: {
    path: path.resolve(__dirname, buildFolder),
    filename: 'index.js',
  },
  devServer: {
    inline: true,
    port: 3333
  },
  plugins: [
    new CleanWebpackPlugin([buildFolder]),
    new HtmlWebpackPlugin({
      template: './src/index.html',
    }),
    new ExtractTextPlugin('/css/main.[hash].css'),
  ],
  module: {
    loaders: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'babel-loader',
        query: { presets: ['es2015', 'react'] }
      },
      {
        test: /\.json$/,
        exclude: /node_modules/,
        loader: 'json-loader',
      },
      {
        test: /\.html$/,
        exclude: /node_modules/,
        loader: 'html',
      },
      {
        test: /\.scss$/,
        loader: ExtractTextPlugin.extract('css!sass'),
      }
    ]
  }
}
