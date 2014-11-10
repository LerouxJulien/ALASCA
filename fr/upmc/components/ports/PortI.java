package fr.upmc.components.ports;

import fr.upmc.components.ComponentI;

// Copyright Jacques Malenfant, Univ. Pierre et Marie Curie.
// 
// Jacques.Malenfant@lip6.fr
// 
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
// 
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
// 
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
// 
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
// 
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

/**
 * The interface <code>PortI</code> provides for a common supertype for all
 * ports in the component model.
 * 
 * <p><strong>Description</strong></p>
 * 
 * In the component model, a port represent a plug through which components
 * can be connected using connectors.  On the implementation side, ports
 * are objects through which a client component calls its provider components,
 * and also through which a provider component is called by its client
 * components.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	this.getPortURI() != null
 * invariant	this.connected() =>
 *                  (this.getPortURI().equals(this.getClientPortURI()) ||
 *                      this.getPortURI().equals(this.getServerPortURI()))
 * </pre>
 * 
 * <p>Created on : 2011-11-07</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		PortI
{
	// ------------------------------------------------------------------------
	// Self-properties management
	// ------------------------------------------------------------------------

	/**
	 * return the component that owns this port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the component that owns this port.
	 * @throws Exception
	 */
	public ComponentI	getOwner() throws Exception ;

	/**
	 * return the interface implemented by this port on behalf of the
	 * component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	ComponentImplementedI.class.isAssignableFrom(return)
	 * </pre>
	 *
	 * @return	the Class<?> object representing the port component's interface
	 */
	public Class<?>		getImplementedInterface() throws Exception ;
	
	/**
	 * return the unique identifier of this port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return	the unique identifier of this port.
	 */
	public String		getPortURI() throws Exception ;

	// ------------------------------------------------------------------------
	// Registry management
	// ------------------------------------------------------------------------

	/**
	 * publish the port on the local registry.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 * @throws Exception 
	 *
	 */
	public void			localPublishPort() throws Exception ;

	/**
	 * publish the port both on the local and the global registry.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 * @throws Exception 
	 *
	 */
	public void			publishPort() throws Exception ;

	/**
	 * unpublish the port on the local registry and on the global one if
	 * required.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 * @throws Exception 
	 *
	 */
	public void			unpublishPort() throws Exception ;

	// ------------------------------------------------------------------------
	// Life-cycle management
	// ------------------------------------------------------------------------

	/**
	 * destroy this port, unpublishing it and removing it from the ports known
	 * to the owner component
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	!this.connected()
	 * pre	this.isPublished()
	 * post	!this.isPublished()
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void			destroyPort() throws Exception ;

	// ------------------------------------------------------------------------
	// Connection management
	// ------------------------------------------------------------------------

	/**
	 *  sets the URI of the client port to which this port is connected.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param clientPortURI
	 * @throws Exception 
	 */
	public void			setClientPortURI(String clientPortURI)
	throws Exception ;

	/**
	 * sets the URI of the server port to which this port is connected.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param serverPortURI
	 * @throws Exception 
	 */
	public void			setServerPortURI(String serverPortURI)
	throws Exception ;

	/**
	 *  sets the URI of the client port to which this port is connected.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception 
	 */
	public void			unsetClientPortURI()
	throws Exception ;

	/**
	 * sets the URI of the server port to which this port is connected.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception 
	 */
	public void			unsetServerPortURI()
	throws Exception ;

	/**
	 * return the URI of the client port in the connection enabled by this port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the URI of the client port.
	 * @throws Exception 
	 */
	public String		getClientPortURI() throws Exception ;

	/**
	 * return the URI of the server port in the connection enabled by this port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the URI of the server port.
	 * @throws Exception 
	 */
	public String		getServerPortURI() throws Exception ;

	/**
	 * check whether or not the port is connected to some connector.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	true if connected to some connector, and false otherwise.
	 */
	public boolean		connected() throws Exception ;

	/**
	 * connect the port where the owner is calling its port to initiate the
	 * connection.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	this.connected()
	 * </pre>
	 *
	 * @param otherPortURI	URI of the other port to be connected with.
	 * @param ccname		connector class name to be used in the connection.
	 * @throws Exception
	 */
	public void			doConnection(String otherPortURI, String ccname)
	throws Exception ;

	/**
	 * connect when the other component is the initiator of the connection or
	 * called by the owner after it has requested the connection from the other
	 * component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	this.connected()
	 * </pre>
	 *
	 * @param otherPortURI	URI of the other port to be connected with.
	 * @param ccname		connector class name to be used in the connection.
	 * @throws Exception
	 */
	public void			obeyConnection(String otherPortURI, String ccname)
	throws Exception ;

	/**
	 * disconnect the port where the owner is calling its port to initiate the
	 * disconnection.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.connected()
	 * post	true			// no precondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void			doDisconnection() throws Exception ;

	/**
	 * disconnect when the other component is the initiator of the disconnection
	 * or called by the owner after it has requested the disconnection from the
	 * other component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	this.connected()
	 * post	true			// no precondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void			obeyDisconnection() throws Exception ;
}
