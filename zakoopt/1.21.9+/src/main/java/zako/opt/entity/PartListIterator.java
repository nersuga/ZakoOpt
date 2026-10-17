package zako.opt.entity;

import net.minecraft.client.model.geom.ModelPart;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

// an index walk over a model's immutable part list, kept by the model and rewound for every use
public final class PartListIterator implements Iterator<ModelPart> {
	private List<ModelPart> parts;
	private int next;

	public PartListIterator reset(List<ModelPart> parts) {
		this.parts = parts;
		next = 0;
		return this;
	}

	@Override
	public boolean hasNext() {
		return next < parts.size();
	}

	@Override
	public ModelPart next() {
		if (next >= parts.size()) {
			throw new NoSuchElementException();
		}
		return parts.get(next++);
	}
}
