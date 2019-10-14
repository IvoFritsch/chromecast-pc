import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import HWApiFetch from 'hw-api-fetch'
 
const properties = {
  host: 'http://localhost:3132/app/api/',
  cookiesToHeader: [],
  beforeReturn: [],
  log: true,
  hwResponse: false
}
 
HWApiFetch.init(properties);

ReactDOM.render(<App />, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
