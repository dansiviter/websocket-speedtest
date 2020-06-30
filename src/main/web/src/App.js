import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Button from '@material-ui/core/Button';
import Fab from '@material-ui/core/Fab';
import CssBaseline from '@material-ui/core/CssBaseline';
import Card from '@material-ui/core/Card';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import CardHeader from '@material-ui/core/CardHeader';
import LinearProgress from '@material-ui/core/LinearProgress';
import withStyles from '@material-ui/core/styles/withStyles';
import Gauge from 'react-svg-gauge';
import { PlayArrow, Report, Done } from '@material-ui/icons';

const styles = theme => ({
	layout: {
		width: 'auto',
		display: 'block', // Fix IE 11 issue.
		marginLeft: theme.spacing(3),
		marginRight: theme.spacing(3),
		[theme.breakpoints.up(800 + theme.spacing(3 * 2))]: {
			width: 490,
			marginLeft: 'auto',
			marginRight: 'auto',
		},
	},
	card: {
		marginTop: theme.spacing(4)
	},
	avatar: {
		margin: theme.spacing(1),
		backgroundColor: theme.palette.secondary.main,
	},
	form: {
		width: '100%', // Fix IE 11 issue.
		marginTop: theme.spacing(1)
	},
	submit: {
		marginTop: theme.spacing(3)
	},
	cardHeader: {
		backgroundColor: theme.palette.grey[200]
	}

});

class App extends Component {
	state = {
			state: 'CLOSED',
			avgRtt: 0,
			jitter: 0,
	};

	componentDidMount = () => {
		this.worker = new Worker('Worker.js');
		this.worker.onmessage = this.onMessage;
		this.worker.onerror = this.onerror;
		this.worker.onmessageerror = this.onerror;
	}

	connect = () => {
		this.worker.postMessage({ type: "RECONNECT" })
	}

	onMessage = e => {
		switch (e.data.type) {
		case 'OPEN':
			console.log('CONNECTED');
			this.setState({
				state: 'CONNECTED'
			});
			break;
		case 'MESSAGE':
			console.log('MESSAGE:' + e.data.msg);
			break;
		case 'RESULTS':
			if (typeof e.data.data === "string") {
				var results = JSON.parse(e.data.data);
				var avgRtt = Math.round(results.avgRtt / 1000000) // nanos
				var jitter = Math.round(results.jitter / 1000000) // nanos
				console.log('Completed. [RTT=%dms,jitter=%dms]', avgRtt, jitter);
				this.setState({
					avgRtt: avgRtt,
					jitter: jitter,
				});
			}
			this.setState({
				state: 'FINISHED'
			});
			break;
		case "CLOSE":
			console.log('DISCONNECTED');
			this.setState({
				state: 'CLOSED'
			});
			break;
		case "ERROR":
			console.log('ERROR');
			break;
		default:
			console.log('Unknown ' + e.data.type);
		}
	}

	onError = e => {
		console.log('Error in WebWorker!', e)
	}

	handleClickStart = () => {
		const warmUpCycles = 50;
		const testCycles = 100;
		const delay = 25;
		console.log('Starting... [warm-up=%d,cycles=%d,delay=%d]', warmUpCycles, testCycles, delay);
		this.worker.postMessage({ type: "START", params: {
			warmUp: warmUpCycles, cycles: testCycles, delay: delay }
		})
		this.setState({
			state: 'STARTED'
		});
	};

	colourHex = (value, max) => {
		let percent = 100 / max * value;
		let g = percent<50 ? 255 : Math.floor(255-(percent*2-100)*255/100);
		let r = percent>50 ? 255 : Math.floor((percent*2)*255/100);
		return 'rgb(' + r + ',' + g + ',0)';
	}

	render() {
		const { classes } = this.props;
		const avgRttHex = this.colourHex(this.state.avgRtt, 50);
		const jitterHex = this.colourHex(this.state.jitter, 20);
		const valueStyle = {
				fontSize: '2em'
		}
		const topStyle = {
		}
		const minMaxStyle = {
			fontSize: '0.75em'
		}

		return (
				<React.Fragment>
					<CssBaseline />
					<main className={ classes.layout }>
						<Card className={ classes.card }>
							<CardHeader
								title="WebSocket Speed Test"
								subheader="Click 'Start' to begin the test."
								className={ classes.cardHeader }
								action={
									<Fab aria-label="Reconnect"
											onClick={ this.connect }
											size="medium"
											disabled={ this.state.state === 'STARTED' }>
										{ this.state.state === 'CLOSED' ? <Report /> : <Done /> }
									</Fab>
								}
							/>
							<LinearProgress value={ 100 } variant={ this.state.state === 'STARTED' ? "indeterminate" : "determinate" }/>
							<CardContent>
							<Gauge width={220} height={140} max={ 50 } label={ 'Avg. RTT' }
									color={avgRttHex} value={ this.state.avgRtt }
									valueFormatter={ value => `${value}ms` }
									topLabelStyle={ topStyle }
									valueLabelStyle={ valueStyle }
									minMaxLabelStyle={ minMaxStyle } />
							<Gauge width={220} height={140} max={ 20 } label={ 'Jitter' }
									color={jitterHex} value={ this.state.jitter }
									valueFormatter={ value => `${value}ms` }
									topLabelStyle={ topStyle }
									valueLabelStyle={ valueStyle }
									minMaxLabelStyle={ minMaxStyle } />
							</CardContent>
							<CardActions className={ classes.cardActions }>
								<Button variant="contained" fullWidth
										disabled={ this.state.state === 'STARTED' }
										color="primary"
										onClick={ this.handleClickStart }
										startIcon={ <PlayArrow /> }>
									Start
								</Button>
							</CardActions>
						</Card>
					</main>
				</React.Fragment>
		);
	}
}

App.propTypes = {
		classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(App);
