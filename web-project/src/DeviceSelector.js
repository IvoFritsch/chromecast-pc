import React, { Component } from "react";
import { Dialog, DialogTitle, List, ListItem, ListItemAvatar, ListItemText, Avatar, CircularProgress } from "@material-ui/core";
import { MdTv } from "react-icons/md";
import HWApiFetch from 'hw-api-fetch'

class DeviceSelector extends Component{

    state = {
        devices: undefined
    }

    searchInterval = undefined;

    componentDidMount(){
        this.searchInterval = setInterval(this.searchDevices, 5000);
    }

    componentWillUnmount(){
        clearInterval(this.searchInterval);
    }

    searchDevices = () => {
        if(!this.props.open) return;
        HWApiFetch.get('list').then(console.log);
        this.setState({devices:[{id:'2143144', title: 'Sala de TV'}]});
    }

    render(){
        return (
            <Dialog onClose={this.props.onClose} aria-labelledby="simple-dialog-title" open={this.props.open} fullWidth maxWidth={'md'}>
                <DialogTitle id="simple-dialog-title">Cast to</DialogTitle>
                {this.state.devices && 
                    <List>
                        {this.state.devices.map(d =>
                            <ListItem key={d.id} button >
                                <ListItemAvatar>
                                    <Avatar>
                                    <MdTv />
                                    </Avatar>
                                </ListItemAvatar>
                                <ListItemText primary={d.title} />
                            </ListItem>
                        )}
                    </List>
                }
                {!this.state.devices && 
                    <div style={{width:'100%', textAlign:'center', height:'60px'}}>
                        <CircularProgress style={{color:'white', fontSize:'15px'}}/>
                    </div>
                }
            </Dialog>
        )
    }
}

export default DeviceSelector;