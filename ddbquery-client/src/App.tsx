import {Link, Route, Routes} from "react-router-dom";
import {Container, Nav, Navbar} from "react-bootstrap";
import Query from "./routes/Query";
import Home from "./routes/Home";
import React from "react";

export default function App() {
    return (
        <div>
            <Navbar bg="light" expand="lg">
                <Container>
                    <Navbar.Brand as={Link} to="/">分布式数据库查询平台</Navbar.Brand>
                    <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                    <Navbar.Collapse id="basic-navbar-nav">
                        <Nav className="mr-auto">
                            <Nav.Link as={Link} to="/query">查询</Nav.Link>
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
            <Container>
                <Routes>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/query" element={<Query/>}/>
                    <Route path="/*" element={<Home/>}/>
                </Routes>
            </Container>

        </div>
    );
}