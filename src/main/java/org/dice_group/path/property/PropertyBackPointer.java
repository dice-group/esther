package org.dice_group.path.property;

/**
 * TODO unnecessary to have a different object type as backpointer, since no
 * added info is needed now
 *
 */
public class PropertyBackPointer {
	private Property property;

	public PropertyBackPointer(Property property) {
		this.property = property;
	}

	/**
	 * Constructor to enable object's deep copy
	 * 
	 * @param backPointer
	 */
	public PropertyBackPointer(PropertyBackPointer backPointer) {
		this.property = new Property(backPointer.getProperty());
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

}
