
import React, {useEffect} from  'react';
import { useState } from 'react';
import FlightTable from './FlightTable.jsx';
import './FlightApp.css'
import FlightForm from "./FlightForm.jsx";
import {GetFlights, DeleteFlight, UpdateFlight, AddFlight} from './utils/rest-calls'


export default function FlightApp(){
	const [flights, setFlights] = useState([]);

	function addFunc(flight){
		console.log('inside add Func '+flight);
		AddFlight(flight)
			.then(res=> GetFlights())
			.then(flights=>setFlights(flights))
			.catch(erorr=>console.log('eroare add ',erorr));
	}

	function deleteFunc(id){
		console.log('inside deleteFunc '+id);
		DeleteFlight(id)
			.then(res=> GetFlights())
			.then(flights=>setFlights(flights))
			.catch(error=>console.log('eroare delete', error));
	}

	function updateFunc(flight) {
		console.log('inside updateFunc ' + flight);
		UpdateFlight(flight.id, flight)
			.then(res=> GetFlights())
			.then(flights=>setFlights(flights))
			.catch(error=>console.log('eroare update', error));
	}

	useEffect(()=>{
		console.log('inside useEffect')
		GetFlights().then(flights=>setFlights(flights));},[]);

	return (<div className="UserApp">
		<FlightForm addFunc={addFunc}/>
		<br/>
		<br/>
		<FlightTable flightsList={flights} updateFunc={updateFunc} deleteFunc={deleteFunc}/>
	</div>);
}

