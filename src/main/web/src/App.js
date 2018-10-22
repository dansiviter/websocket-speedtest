import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import Card from '@material-ui/core/Card';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import CardHeader from '@material-ui/core/CardHeader';
import withStyles from '@material-ui/core/styles/withStyles';
import Gauge from 'react-svg-gauge';

const styles = theme => ({
	layout: {
		width: 'auto',
		display: 'block', // Fix IE 11 issue.
		marginLeft: theme.spacing.unit * 3,
		marginRight: theme.spacing.unit * 3,
		[theme.breakpoints.up(800 + theme.spacing.unit * 3 * 2)]: {
			width: 800,
			marginLeft: 'auto',
			marginRight: 'auto',
		},
	},
	card: {
		marginTop: theme.spacing.unit * 4
	},
	avatar: {
		margin: theme.spacing.unit,
		backgroundColor: theme.palette.secondary.main,
	},
	form: {
		width: '100%', // Fix IE 11 issue.
		marginTop: theme.spacing.unit,
	},
	submit: {
		marginTop: theme.spacing.unit * 3,
	},
	cardHeader: {
		backgroundColor: theme.palette.grey[200],
	}
});

class App extends Component {
	state = {
			state: null,
			avgRtt: 0,
			jitter: 0,
			drift: 0
	};

	componentDidMount = () => {
		this.worker = new Worker('Worker.js');
		this.worker.onmessage = this.onMessage;
	}

	onMessage = e => {
		switch (e.data.type) {
		case 'OPEN':
			console.log('CONNECTED');
			break;
		case 'MESSAGE':
			console.log('MESSAGE:' + e.data.msg);
			break;
		case 'RESULTS':
			var timestamp = e.data.timestamp;
			console.log('Server [' + new Date(timestamp).toISOString() + ']: ' + e.data.data);
			if (typeof e.data.data === "string") {
				var results = JSON.parse(e.data.data);
				var avgRtt = Math.round(results.avgRtt / 1000000) // nanos
				var jitter = Math.round(results.jitter / 1000000) // nanos
				var serverTimestamp = new Date(results.timestamp);
				var clockDiff = timestamp - serverTimestamp;
				var clockDrift = (clockDiff - avgRtt) / 2;
				console.log('RTT=' + avgRtt + 'ms, Jitter=' + jitter + 'ms, diff=' + clockDiff + ', drift: ' + clockDrift + 'ms');
				this.setState({
					avgRtt: avgRtt,
					jitter: jitter,
					drift: clockDrift
				});
			}
			this.setState({
				state: 'FINISHED'
			});
			break;
		case "CLOSE":
			console.log('DISCONNECTED');
			break;
		case "ERROR":
			console.log('ERROR');
			break;
		default:
			console.log('Unknown ' + e.data.type);
		}
	}
	
	handleClickStart = () => {
		const warmUpCycles = '10';
		const testCycles = '10';
		console.log('Starting - warm-up=' + warmUpCycles + ', test=' + testCycles);
		this.worker.postMessage({ type: "START", params: {
			warmUp: warmUpCycles, cycles: testCycles }
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
		const driftHex = this.colourHex(this.state.drift, 50);
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
							/>
							<CardContent>
							<Gauge width={220} height={140} max={ 50 } label={ 'Average RTT' }
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
							<Gauge width={220} height={140} max={ 50 } label={ 'Clock Drift' }
									color={driftHex} value={ this.state.drift }
									valueFormatter={ value => `${value}ms` } 
									topLabelStyle={ topStyle }
									valueLabelStyle={ valueStyle }
									minMaxLabelStyle={ minMaxStyle } />
							</CardContent>
							<CardActions className={ classes.cardActions }>
								<Button variant="contained" fullWidth
										disabled={ this.state.state === 'STARTED' }
										color="primary"
										onClick={ this.handleClickStart }>
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