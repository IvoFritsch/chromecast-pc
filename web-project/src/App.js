import React, { Component } from 'react';
import { List, ListItem, Avatar, ListItemIcon, ListItemText, createMuiTheme, MuiThemeProvider, CssBaseline, Paper, Link, Typography, Breadcrumbs, IconButton, ElevationScroll, AppBar, Toolbar, Button, Dialog, DialogTitle, ListItemAvatar } from '@material-ui/core';
import { MdFolder, MdLocalMovies, MdKeyboardArrowRight, MdPlayArrow, MdCast, MdTv, MdStar } from "react-icons/md";
import { useTheme, ThemeProvider } from '@material-ui/styles';
import './App.css';
import DeviceSelector from './DeviceSelector';

class App extends Component{

  state = {
    selectingDevice: false
  }

  theme = createMuiTheme({
    palette: {
      type: 'dark',
    },
  });

  render() {
    return (
      <MuiThemeProvider theme={this.theme}>
      <div className="App">
        <CssBaseline />
          <AppBar color={'inherit'}>
            <Toolbar style={{width:'100%'}}>
              <Typography edge="start" variant="h6">Chromecast PC</Typography>
                <IconButton color="inherit" onClick={() => this.setState({selectingDevice: true})}>
                  <MdCast />
                </IconButton>
            </Toolbar>
          </AppBar>
          <br/>
          <div style={{padding:'20px'}}>
          <Typography variant="h6">Welcome to Chromecast PC</Typography>
          <br/>
          <Typography variant="h7">You're not connected to any device.</Typography>
          <Typography style={{color:'#00aae1'}}>To start casting, click the <MdCast style={{fontSize:'15px'}}/> icon above.</Typography>
          </div>
          
          <DeviceSelector onClose={() => this.setState({selectingDevice: false})} open={this.state.selectingDevice}/>

        
      </div>
      </MuiThemeProvider>
    );
  }
}

export default App;


/* Lista de tarefas
          <List>
            <ListItem button >
              <ListItemAvatar>
                <Avatar>
                  <MdStar />
                </Avatar>
              </ListItemAvatar>
              <ListItemText primary="See your favorites" />
            </ListItem>
            <ListItem button >
              <ListItemAvatar>
                <Avatar>
                  <MdTv />
                </Avatar>
              </ListItemAvatar>
              <ListItemText primary="Browse your PC" />
            </ListItem>
          </List>
*/



/*
<div style={{position:'fixed', top: '0px', left: '0px', height:'48px', width:'100%'}}>
        <div className='scrollmenu'>
          <a href="#home">C</a>
          <MdKeyboardArrowRight/>
          <a href="#news">Windows</a>
          <MdKeyboardArrowRight/>
          <a href="#contact">Users</a>
          <MdKeyboardArrowRight/>
          <a href="#about">Pedrivo</a>
          <MdKeyboardArrowRight/>
          <a href="#news">Videos</a>
          <MdKeyboardArrowRight/>
          <a href="#contact">Chernobyl</a>
        </div>
      </div>
      <List>
        <ListItem button>
            <ListItemIcon>
              <MdFolder style={{fontSize:'28px'}}/>
            </ListItemIcon>
            <ListItemText primary="Legendas" />
            <MdKeyboardArrowRight style={{fontSize:'28px'}}/>
          </ListItem>
        <ListItem button>
            <ListItemIcon>
              <MdLocalMovies style={{fontSize:'28px'}}/>
            </ListItemIcon>
            <ListItemText primary="Ep.1.mp4" />
          </ListItem>
        <ListItem button>
            <ListItemIcon>
              <MdLocalMovies style={{fontSize:'28px'}}/>
            </ListItemIcon>
            <ListItemText primary="Ep.2.mp4" />
          </ListItem>
        <ListItem button>
            <ListItemIcon>
              <MdLocalMovies style={{fontSize:'28px'}}/>
            </ListItemIcon>
            <ListItemText primary="Ep.3.mp4" />
          </ListItem>
        <ListItem button>
            <ListItemIcon>
              <MdLocalMovies style={{fontSize:'28px'}}/>
            </ListItemIcon>
            <ListItemText primary="Ep.4.mp4" />
          </ListItem>
      </List>
      <div style={{position:'fixed', bottom: '0px', left: '0px', height:'53px', width:'100%', background: '#00aae1', padding:'3px'}}>
        <div style={{height:'100%', width: '80%', float:'left', overflow:'hidden', textOverflow:'ellipsis', whiteSpace: 'nowrap', padding:'3px'}}>
          <p style={{margin:'0px', textOverflow:'ellipsis'}}>Ep.1</p>
          <p style={{margin:'0px', fontSize:'13px', opacity:'0.8'}}>C:/Windows/Users/Pedrivo/Videos/Chernobyl/</p>
        </div>
        <div style={{height:'100%', width: '20%', float:'left', textAlign:'center'}}>
        <IconButton color="white" >
          <MdPlayArrow style={{}}/>
        </IconButton>
        </div>
      </div>

*/