
import React, {useEffect, useState} from 'react';
import './FlightApp.css'

function FlightRow({flight, updateFunc, deleteFunc}){
    const ms = Number(flight.departureTimeMillis);
    const dt = new Date(ms);
    const dateStr = dt.toLocaleDateString('ro-RO');
    const [seats, setSeats] = useState(flight.numberOfAvailableSeats);
    useEffect(() => {
        setSeats(flight.numberOfAvailableSeats);
    }, [flight.numberOfAvailableSeats]);
    function handleDelete(event){
        console.log('delete button pentru '+flight.id);
        deleteFunc(flight.id);
    }

    function handleUpdate(event) {
        console.log('update button pentru ' + flight.id);
        flight.numberOfAvailableSeats = Number(seats)
        updateFunc(flight)
    }
    return (
        <tr>
            <td>{flight.id}</td>
            <td>{flight.airport}</td>
            <td>{flight.destination}</td>
            <td><input type="number" value={seats} onChange={e => setSeats(e.target.value)}/> </td>
            <td>{dateStr}</td>
            <td>{flight.departureTime}</td>
            <td><button  onClick={handleUpdate}>Update</button></td>
            <td><button  onClick={handleDelete}>Delete</button></td>
        </tr>
    );
}


export default function FlightTable({flightsList, updateFunc, deleteFunc}){
    console.log("In FlightTable");
    console.log(flightsList);
    let rows = [];
    let functieStergere=deleteFunc;
    let functieActualizare = updateFunc;
    flightsList.forEach(function(flight) {
        console.log(flight);
        rows.push(<FlightRow flight={flight} key={flight.id} updateFunc={functieActualizare} deleteFunc={functieStergere} />);
    });

    return (
        <div className="FlightTable">
            <table className="center">
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Airport</th>
                    <th>Destination</th>
                    <th>Number of available seats</th>
                    <th>Date</th>
                    <th>Time</th>
                </tr>
                </thead>
                <tbody>{rows}</tbody>
            </table>

        </div>
    );
}

