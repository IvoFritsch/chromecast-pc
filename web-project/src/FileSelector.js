import React, { Component } from "react";
import { Dialog, DialogTitle, List, ListItem, ListItemAvatar, ListItemText, Avatar, CircularProgress, ListItemIcon, IconButton, Button, Typography } from "@material-ui/core";
import { MdTv, MdKeyboardArrowRight, MdFolder, MdLocalMovies, MdPlayArrow, MdStorage } from "react-icons/md";
import HWApiFetch from 'hw-api-fetch'

class FileSelector extends Component{

    state = {
        filesList: [],
        currentDirectory:[], 
        isError: undefined,
        loading: true
    }

    searchInterval = undefined;

    componentDidMount(){
        this.searchDirectory();
        this.searchInterval = setInterval(() => this.searchDirectory(true), 2000);
    }

    componentWillUnmount(){
        clearInterval(this.searchInterval);
    }

    searchDirectory = (refresh) => {
        this.setState(refresh ? {} : {loading:true, filesList: []}, () => {
            HWApiFetch.get('list-directory/' + this.state.currentDirectory.join('/')).then(r => this.setState({filesList: r.files || [], isError: r.error, loading:false}, 
                (refresh ? undefined : () => document.getElementById("scrollmenu").scrollTo({
                    left: 100000,
                    behavior: 'smooth'
                }))
            ));
        });
    }

    serveFile = (file) => {
        HWApiFetch.get('stop-stream-serve').then( () => {
            HWApiFetch.get('start-stream-serve/' + this.state.currentDirectory.join('/') + '/' + file);
        });
    }

    resolveItemIcon(f){
        switch(f.type){
            case 'drive':
                return <MdStorage style={{fontSize:'28px'}}/>;
            case 'folder':
                return <MdFolder style={{fontSize:'28px'}}/>;
            case 'file':
                return <MdLocalMovies style={{fontSize:'28px'}}/>;
        }
    }

    enterDirectory = (dir) => {
        const { currentDirectory } = this.state;
        currentDirectory.push(dir);
        this.setState({ currentDirectory }, this.searchDirectory);
    }

    backToDir = (count) => {
        const { currentDirectory } = this.state;
        this.setState({ currentDirectory: currentDirectory.slice(0,count) }, this.searchDirectory);
    }

    render(){
        return (
            <div style={{position:'fixed', top: '104px', left: '0px', height:'calc(100% - 104px)', width:'100%', background:'#303030', overflow:'auto'}}>
            <div style={{top: '56px', left: '0px', height:'48px', width:'100%', position:'fixed'}}>
                <div className='scrollmenu' id='scrollmenu'>
                    <>
                        <Button onClick={() => this.backToDir(0)} style={{height:'100%'}}>Root</Button>
                        <MdKeyboardArrowRight/>
                    </>
                {this.state.currentDirectory.map((d, index) => 
                    <React.Fragment key={index}>
                        <Button onClick={() => this.backToDir(index + 1)} style={{height:'100%'}}>{d}</Button>
                        <MdKeyboardArrowRight/>
                    </React.Fragment>
                )}
                </div>
            </div>
            <List>
                {this.state.filesList.map(f => 
                    <ListItem button onClick={() => {
                        if(f.type === 'folder' || f.type === 'drive') this.enterDirectory(f.name);
                        if(f.type === 'file') this.serveFile(f.name);
                    }}>
                        <ListItemIcon>
                            {this.resolveItemIcon(f)}
                        </ListItemIcon>
                        <ListItemText primary={f.name} />
                        {f.type === 'folder' &&
                            <MdKeyboardArrowRight style={{fontSize:'28px'}}/>
                        }
                    </ListItem>
                )}
            </List>
            {this.state.loading &&
                <div style={{width:'100%', textAlign:'center', paddingTop:'30px'}}>
                    <CircularProgress style={{color:'white'}}/>
                </div>
            }
            {(this.state.filesList.length === 0 && !this.state.loading) && 
                (this.state.isError ? 
                <Typography variant='body1' style={{padding: '10px', color: 'red', textAlign: 'center'}}>Cannot access this directory, verify if the Chromecast-PC microserver has access permissions here.</Typography>
                    :    
                <Typography variant='body1' style={{padding: '10px'}}>There's no supported file here.</Typography>
                )
            }
            </div>
        )
    }
}

export default FileSelector;